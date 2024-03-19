package com.sismics.books.core.model.importStrategies.book;

import com.google.common.base.Strings;
import com.google.common.util.concurrent.RateLimiter;
import com.neovisionaries.i18n.LanguageCode;
import com.sismics.books.core.model.jpa.Book;
import com.sismics.books.core.service.BookDataService;
import com.sismics.books.core.util.DirectoryUtil;
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
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class OpenLibraryImport implements BookImportStrategy{

    /**
     * Logger.
     */
    private static final Logger log = LoggerFactory.getLogger(OpenLibraryImport.class);

    /**
     * Open Library API URL.
     */
    private static final String OPEN_LIBRARY_FORMAT = "http://openlibrary.org/api/volumes/brief/isbn/%s.json";
    private static final int CONNECT_TIMEOUT = 10000;
    private static final int READ_TIMEOUT = 10000;

    /**
     * Executor for book API requests.
     */
    private ExecutorService executor;

    /**
     * Open Library API rate limiter.
     */
    private RateLimiter openLibraryRateLimiter = RateLimiter.create(0.33332);

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
        executor = Executors.newSingleThreadExecutor();
        if (log.isInfoEnabled()) {
            log.info("Book data service started");
        }
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
     * Search a book by its ISBN using Open Library.
     *
     * @return Book found
     * @throws Exception
     */
    private ObjectNode searchBookWithOpenLibrary(String isbn) throws Exception {
        openLibraryRateLimiter.acquire();

        InputStream inputStream = getStream(String.format(Locale.ENGLISH, OPEN_LIBRARY_FORMAT, isbn));
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readValue(inputStream, JsonNode.class);
        if (rootNode instanceof ArrayNode) {
            throw new Exception("No book found for ISBN: " + isbn);
        }

        JsonNode bookNode = rootNode.get("records").getElements().next();


        ObjectNode parseddata = mapper.createObjectNode();

        JsonNode details = bookNode.get("details").get("details");
        JsonNode data = bookNode.get("data");


        // set fields in parseddata
        parseddata.put("id",UUID.randomUUID().toString());
        parseddata.put("title",details.get("title").getTextValue());
        parseddata.put("subtitle", details.has("subtitle") ? details.get("subtitle").getTextValue() : null);
        if (!data.has("authors") || data.get("authors").size() == 0) {
            throw new Exception("Book without author for ISBN: " + isbn);
        }
        parseddata.put("author",data.get("authors").get(0).get("name").getTextValue());
        parseddata.put("description",details.has("first_sentence") ? details.get("first_sentence").get("value").getTextValue() : null);
        parseddata.put("isbn10",details.has("isbn_10") && details.get("isbn_10").size() > 0 ? details.get("isbn_10").get(0).getTextValue() : null);
        parseddata.put("isbn13",details.has("isbn_13") && details.get("isbn_13").size() > 0 ? details.get("isbn_13").get(0).getTextValue() : null);
        if (details.has("languages") && details.get("languages").size() > 0) {
            String language = details.get("languages").get(0).get("key").getTextValue();
            LanguageCode languageCode = LanguageCode.getByCode(language.split("/")[2]);
            parseddata.put("language",languageCode.name());
        }
        parseddata.put("pageCount",details.has("number_of_pages") ? details.get("number_of_pages").getLongValue() : null);
        if (!details.has("publish_date")) {
            throw new Exception("Book without publication date for ISBN: " + isbn);
        }
        parseddata.put("publishDate", details.has("publish_date") ? details.get("publish_date").getTextValue() : null);

        // Download the thumbnail
        if (details.has("covers") && details.get("covers").size() > 0) {
            String imageUrl = "http://covers.openlibrary.org/b/id/" + details.get("covers").get(0).getLongValue() + "-M.jpg";
            downloadThumbnail(parseddata.get("id").getTextValue(), imageUrl);
        }


        return parseddata;
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

    public ObjectNode pullData(String isbn) throws Exception {
        validateIsbn(isbn);
        ObjectNode data = searchBookWithOpenLibrary(isbn);
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
