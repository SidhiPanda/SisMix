package com.sismics.books.core.model.importStrategies.book;

import com.google.common.base.Strings;
import com.google.common.util.concurrent.RateLimiter;
import com.sismics.books.core.constant.ConfigType;
import com.sismics.books.core.model.jpa.Book;
import com.sismics.books.core.service.BookDataService;
import com.sismics.books.core.util.ConfigUtil;
import com.sismics.books.core.util.DirectoryUtil;
import com.sismics.books.core.util.TransactionUtil;
import com.sismics.books.core.util.mime.MimeType;
import com.sismics.books.core.util.mime.MimeTypeUtil;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;
import org.joda.time.format.DateTimeParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Iterator;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.*;

public class GoogleImport implements BookImportStrategy{

    /**
     * Logger.
     */
    private static final Logger log = LoggerFactory.getLogger(GoogleImport.class);


    /**
     * Google Books API Search URL.
     */
    private static final String GOOGLE_BOOKS_SEARCH_FORMAT = "https://www.googleapis.com/books/v1/volumes?q=isbn:%s&key=%s";

    private static final int CONNECT_TIMEOUT = 10000;
    private static final int READ_TIMEOUT = 10000;


    /**
     * Executor for book API requests.
     */
    private ExecutorService executor;

    /**
     * Google API rate limiter.
     */
    private RateLimiter googleRateLimiter = RateLimiter.create(20);

    /**
     * API key Google.
     */
    private String apiKeyGoogle = null;

    /**
     * Parser for multiple date formats;
     */
    private static DateTimeFormatter formatter;


    static {
        // Initialize date parser
        DateTimeParser[] parsers = {
                DateTimeFormat.forPattern("yyyy").getParser(),
                DateTimeFormat.forPattern("yyyy-MM").getParser(),
                DateTimeFormat.forPattern("yyyy-MM-dd").getParser(),
                DateTimeFormat.forPattern("MMM d, yyyy").getParser()};
        formatter = new DateTimeFormatterBuilder().append( null, parsers ).toFormatter();
    }


    protected void startUp() throws Exception {
        initConfig();
        executor = Executors.newSingleThreadExecutor();
        if (log.isInfoEnabled()) {
            log.info("Book data service started");
        }
    }

    /**
     * Initialize service configuration.
     */
    public void initConfig() {
        TransactionUtil.handle(new Runnable() {
            @Override
            public void run() {
                apiKeyGoogle = ConfigUtil.getConfigStringValue(ConfigType.API_KEY_GOOGLE);
            }
        });
    }

    /**
     * Search a book by its ISBN.
     *
     * @return Book found
     * @throws Exception
     */
    public void validateIsbn(String rawIsbn) throws Exception {
        // Sanitize ISBN (keep only digits)
        final String isbn = rawIsbn.replaceAll("[^\\d]", "");

        // Validate ISBN
        if (Strings.isNullOrEmpty(isbn)) {
            throw new Exception("ISBN is empty");
        }
        if (isbn.length() != 10 && isbn.length() != 13) {
            throw new Exception("ISBN must be 10 or 13 characters long");
        }

//        Callable<Book> callable = new Callable<Book>() {
//
//            @Override
//            public Book call() throws Exception {
//                try {
//                    return searchBookWithGoogle(isbn);
//                } catch (Exception e) {
//                    log.warn("Book not found with Google: " + isbn + " with error: " + e.getMessage());
//                }
//            }
//        };
//        FutureTask<Book> futureTask = new FutureTask<Book>(callable);
//        executor.submit(futureTask);
//
//        return futureTask.get();
    }


    /**
     * Search a book by its ISBN using Google Books.
     *
     * @return Book found
     * @throws Exception
     */
    private ObjectNode searchBookWithGoogle(String isbn) throws Exception {
        googleRateLimiter.acquire();

        InputStream inputStream = getStream(String.format(Locale.ENGLISH, GOOGLE_BOOKS_SEARCH_FORMAT, isbn, apiKeyGoogle));
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readValue(inputStream, JsonNode.class);
        ArrayNode items = (ArrayNode) rootNode.get("items");
        if (rootNode.get("totalItems").getIntValue() <= 0) {
            throw new Exception("No book found for ISBN: " + isbn);
        }
        JsonNode item = items.get(0);
        JsonNode volumeInfo = item.get("volumeInfo");

        ObjectNode parseddata = mapper.createObjectNode();

        // Build the book
        parseddata.put("id", UUID.randomUUID().toString());
        parseddata.put("title", volumeInfo.get("title").getTextValue());
        parseddata.put("subtitle", volumeInfo.has("subtitle") ? volumeInfo.get("subtitle").getTextValue() : null);
        ArrayNode authors = (ArrayNode) volumeInfo.get("authors");
        if (authors.size() <= 0) {
            throw new Exception("Author not found");
        }
        parseddata.put("author", authors.get(0).getTextValue());
        parseddata.put("description", volumeInfo.has("description") ? volumeInfo.get("description").getTextValue() : null);
        ArrayNode industryIdentifiers = (ArrayNode) volumeInfo.get("industryIdentifiers");
        Iterator<JsonNode> iterator = industryIdentifiers.getElements();
        while (iterator.hasNext()) {
            JsonNode industryIdentifier = iterator.next();
            if ("ISBN_10".equals(industryIdentifier.get("type").getTextValue())) {
                parseddata.put("isbn10", industryIdentifier.get("identifier").getTextValue());
            } else if ("ISBN_13".equals(industryIdentifier.get("type").getTextValue())) {
                parseddata.put("isbn13", industryIdentifier.get("identifier").getTextValue());
            }
        }
        parseddata.put("language", volumeInfo.get("language").getTextValue());
        parseddata.put("pageCount", volumeInfo.has("pageCount") ? volumeInfo.get("pageCount").getLongValue() : null);
        parseddata.put("publishDate", volumeInfo.get("publishedDate").getTextValue());

        // Download the thumbnail
        JsonNode imageLinks = volumeInfo.get("imageLinks");
        if (imageLinks != null && imageLinks.has("thumbnail")) {
            String imageUrl = imageLinks.get("thumbnail").getTextValue();
            downloadThumbnail(UUID.randomUUID().toString(), imageUrl);
        }


        return parseddata;
    }

    private InputStream getStream(String ENGLISH) throws IOException {
        URL url = new URL(ENGLISH);
        URLConnection connection = url.openConnection();
        connection.setRequestProperty("Accept-Charset", "utf-8");
        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/29.0.1547.62 Safari/537.36");
        connection.setConnectTimeout(CONNECT_TIMEOUT);
        connection.setReadTimeout(READ_TIMEOUT);
        return connection.getInputStream();
    }

    /**
     * Download and overwrite the thumbnail for a book.
     *
     * @param book Book
     * @param imageUrl Image URL
     * @throws Exception
     */
    public void downloadThumbnail(String bookId, String imageUrl) throws Exception {
        URLConnection imageConnection = new URL(imageUrl).openConnection();
        imageConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/29.0.1547.62 Safari/537.36");
        imageConnection.setConnectTimeout(CONNECT_TIMEOUT);
        imageConnection.setReadTimeout(READ_TIMEOUT);
        try (InputStream inputStream = new BufferedInputStream(imageConnection.getInputStream())) {
            if (MimeTypeUtil.guessMimeType(inputStream) != MimeType.IMAGE_JPEG) {
                throw new Exception("Only JPEG images are supported as thumbnails");
            }

            Path imagePath = Paths.get(DirectoryUtil.getBookDirectory().getPath(), bookId);
            Files.copy(inputStream, imagePath, StandardCopyOption.REPLACE_EXISTING);

            // TODO Rescale to 192px width max if necessary
        }
    }


    public ObjectNode pullData(String isbn) throws Exception{
        validateIsbn(isbn);
        ObjectNode data = searchBookWithGoogle(isbn);

        return data;
    }

    protected void shutDown() throws Exception {
        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);
        if (log.isInfoEnabled()) {
            log.info("Book data service stopped");
        }
    }
}
