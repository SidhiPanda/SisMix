package com.sismics.books.rest.resource;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.google.common.base.Strings;
import com.sismics.books.core.dao.jpa.BookDao;
import com.sismics.books.core.model.context.AppContext;
import com.sismics.books.core.model.jpa.Book;
import com.sismics.books.core.model.jpa.UserBook;
import com.sismics.rest.exception.ClientException;
import com.sismics.rest.util.ValidationUtil;

/**
 * Book REST resources.
 *
 * @author bgamard
 */

public class HelperBookResource {

    // MIN_HEADER_LENGTH defines minimum length of title, subtitle and author
    private static final int MIN_HEADER_LENGTH = 1;

    // MAX_HEADER_LENGTH defines maximum length of title, subtitle and author
    private static final int MAX_HEADER_LENGTH = 255;

    // minimum length of description
    private static final int MIN_DESC_LENGTH = 1;

    // maximum length of description
    private static final int MAX_DESC_LENGTH = 4000;

    // Validate the Book data
    public void validateBookData(
            String title,
            String subtitle,
            String author,
            String description,
            String isbn10,
            String isbn13,
            Long pageCount,
            String language,
            String publishDateStr) throws JSONException {

        // Validate input data
        title = ValidationUtil.validateLength(title, "title", MIN_HEADER_LENGTH, MAX_HEADER_LENGTH, true);
        subtitle = ValidationUtil.validateLength(subtitle, "subtitle", MIN_HEADER_LENGTH, MAX_HEADER_LENGTH, true);
        author = ValidationUtil.validateLength(author, "author", MIN_HEADER_LENGTH, MAX_HEADER_LENGTH, true);
        description = ValidationUtil.validateLength(description, "description", MIN_DESC_LENGTH, MAX_DESC_LENGTH, true);
        isbn10 = ValidationUtil.validateLength(isbn10, "isbn10", 10, 10, true);
        isbn13 = ValidationUtil.validateLength(isbn13, "isbn13", 13, 13, true);
        language = ValidationUtil.validateLength(language, "language", 2, 2, true);
        Date publishDate = ValidationUtil.validateDate(publishDateStr, "publish_date", true);
    }

    private ObjectNode genObjectNode(String bookId,
                                     String title,
                                     String subtitle,
                                     String author,
                                     String description,
                                     String isbn10,
                                     String isbn13,
                                     Long pageCount,
                                     String language,
                                     String publishDateStr
                                    ) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode parseddata = mapper.createObjectNode();

        parseddata.put("id", bookId);
        parseddata.put("title", title);
        parseddata.put("subtitle", subtitle);
        parseddata.put("author", author);
        parseddata.put("description", description);
        parseddata.put("isbn10", isbn10);
        parseddata.put("isbn13", isbn13);
        parseddata.put("pageCount", pageCount);
        parseddata.put("language", language);
        parseddata.put("publish_date", publishDateStr);

        return parseddata;
    }

    private void setBookAttributes(Book book,
                                   String title,
                                   String subtitle,
                                   String author,
                                   String description,
                                   String isbn10,
                                   String isbn13,
                                   Long pageCount,
                                   String language,
                                   String publishDateStr) throws JSONException {

        Date publishDate = ValidationUtil.validateDate(publishDateStr, "publish_date", true);

        if (title != null) {
            book.setTitle(title);
        }
        if (subtitle != null) {
            book.setSubtitle(subtitle);
        }
        if (author != null) {
            book.setAuthor(author);
        }
        if (description != null) {
            book.setDescription(description);
        }
        if (isbn10 != null) {
            book.setIsbn10(isbn10);
        }
        if (isbn13 != null) {
            book.setIsbn13(isbn13);
        }
        if (pageCount != null) {
            book.setPageCount(pageCount);
        }
        if (language != null) {
            book.setLanguage(language);
        }
        if (publishDate != null) {
            book.setPublishDate(publishDate);
        }
    }


    /**
     * Creates a new book.
     *
     * @param isbn ISBN Number
     * @return Response
     * @throws JSONException
     */
    public Book add(
            String isbn) throws JSONException {

        // Fetch the book
        BookDao bookDao = new BookDao();
        Book book = bookDao.getByIsbn(isbn);
        if (book == null) {
            // Try to get the book from a public API
            try {
                book = AppContext.getInstance().getBookDataService().searchBook(isbn);

            } catch (Exception e) {
                throw new ClientException("BookNotFound", e.getCause().getMessage(), e);
            }

            // Save the new book in database
            bookDao.create(book);
        }

        return book;
    }

    /**
     * Add a book manually.
     *
     * @param title       Title
     * @param description Description
     * @return Response
     * @throws JSONException
     */

    public Book addManual(
            String title,
            String subtitle,
            String author,
            String description,
            String isbn10,
            String isbn13,
            Long pageCount,
            String language,
            String publishDateStr,
            List<String> tagList) throws JSONException {

        // Validate input data
        if(title == null) {
            throw new ClientException("ValidationError", "Title cannot be empty");
        }
        if(author == null) {
            throw new ClientException("ValidationError", "Author cannot be empty");
        }
        if(publishDateStr == null) {
            throw new ClientException("ValidationError", "Publish Date cannot be empty");
        }

        validateBookData(title, subtitle, author, description, isbn10, isbn13, pageCount, language, publishDateStr);

        if (Strings.isNullOrEmpty(isbn10) && Strings.isNullOrEmpty(isbn13)) {
            throw new ClientException("ValidationError", "At least one ISBN number is mandatory");
        }

        // Check if this book is not already in database
        BookDao bookDao = new BookDao();
        Book bookIsbn10 = bookDao.getByIsbn(isbn10);
        Book bookIsbn13 = bookDao.getByIsbn(isbn13);
        if (bookIsbn10 != null || bookIsbn13 != null) {
            throw new ClientException("BookAlreadyAdded", "Book already added");
        }

        // Create the book
        Book book = new Book();
        book.setId(UUID.randomUUID().toString());

        setBookAttributes(book, title, subtitle, author, description, isbn10, isbn13, pageCount, language, publishDateStr);

        bookDao.create(book);

        return book;
    }

    /**
     * Updates the book.
     *
     * @param title       Title
     * @param description Description
     * @return Response
     * @throws JSONException
     */
    public void update(
            Book book,
            String title,
            String subtitle,
            String author,
            String description,
            String isbn10,
            String isbn13,
            Long pageCount,
            String language,
            String publishDateStr
            ) throws JSONException {

        // Validate input data
        validateBookData(title, subtitle, author, description, isbn10, isbn13, pageCount, language, publishDateStr);

        // Update the book
        setBookAttributes(book, title, subtitle, author, description, isbn10, isbn13, pageCount, language, publishDateStr);
    }

    /**
     * Get a book.
     *
     * @param id User book ID
     * @return Response
     * @throws JSONException
     */

    public void putDetails(
            JSONObject book,
            UserBook userBook,
            Book bookDb) throws JSONException {

        book.put("id", userBook.getId());
        book.put("title", bookDb.getTitle());
        book.put("subtitle", bookDb.getSubtitle());
        book.put("author", bookDb.getAuthor());
        book.put("page_count", bookDb.getPageCount());
        book.put("description", bookDb.getDescription());
        book.put("isbn10", bookDb.getIsbn10());
        book.put("isbn13", bookDb.getIsbn13());
        book.put("language", bookDb.getLanguage());
        if (bookDb.getPublishDate() != null) {
            book.put("publish_date", bookDb.getPublishDate().getTime());
        }
        book.put("create_date", userBook.getCreateDate().getTime());
        if (userBook.getReadDate() != null) {
            book.put("read_date", userBook.getReadDate().getTime());
        }
    }
}