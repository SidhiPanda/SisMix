package com.sismics.books.core.model.builder;

import com.sismics.books.core.constant.Constants;
import com.sismics.books.core.model.importStrategies.book.BookImportContext;
import com.sismics.books.core.model.importStrategies.book.GoogleImport;
import com.sismics.books.core.model.jpa.Book;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;
import org.joda.time.format.DateTimeParser;

import java.util.Date;
import java.util.Iterator;
import java.util.UUID;

public class BookBuilder implements MediaBuilder {

    private Book book;

    // put in director

//    BookImportContext pullMethod;
//    ObjectMapper mapper = new ObjectMapper();
//    ObjectNode data = mapper.createObjectNode();

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

    public void createMedia() {

        this.book = new Book();
    }



    public void setCoreContent(ObjectNode data) {
//        this.book.setId(data.get("id").getTextValue());
//        this.book.setTitle(data.get("title").getTextValue());
//        this.book.setAuthor(data.get("author").getTextValue());
//        this.book.setIsbn10(data.get("isbn10").getTextValue());
//        this.book.setIsbn13(data.get("isbn13").getTextValue());

        String id = data.get("id").getTextValue();
        if (id != null && id.length() <= Constants.MAX_ID_LEN) this.book.setId(id);

        String title = data.get("title").getTextValue();
        if (title != null && title.length() <= Constants.MAX_TITLE_LEN) this.book.setTitle(title);

        String author = data.get("author").getTextValue();
        if (author != null && author.length() <= Constants.MAX_TITLE_LEN) this.book.setAuthor(author);

        String isbn10 = data.get("isbn10").getTextValue();
        if (isbn10 != null && isbn10.length() <= 10) this.book.setIsbn10(isbn10);

        String isbn13 = data.get("isbn13").getTextValue();
        if (isbn13 != null && isbn13.length() <= 13) this.book.setIsbn13(isbn13);
    }


    public void setMediaProfile(ObjectNode data) {
//        this.book.setDescription(data.get("description").getTextValue());
//        this.book.setSubtitle(data.get("subtitle").getTextValue());

        String subtitle = data.get("subtitle").getTextValue();
        if (subtitle != null && subtitle.length() <= Constants.MAX_TITLE_LEN) this.book.setSubtitle(subtitle);

        String description = data.get("description").getTextValue();
        if (description != null && description.length() <= Constants.MAX_DESCRIPTION_LEN) this.book.setDescription(description);
    }


    public void setMetaData(ObjectNode data) {
//        this.book.setLanguage(data.get("language").getTextValue());
//        this.book.setPageCount(data.get("pageCount").getLongValue());
//        this.book.setPublishDate(formatter.parseDateTime(data.get("publish_date").getTextValue()).toDate());

        String language = data.get("language").getTextValue();
        if (language != null && language.length() <= Constants.MAX_LANGUAGE_LEN) this.book.setLanguage(language);

        Long pageCount = data.get("pageCount").getLongValue();
        this.book.setPageCount(pageCount);

        String publishDate = data.get("publish_date").getTextValue();
        if (publishDate != null) {
            this.book.setPublishDate(formatter.parseDateTime(publishDate).toDate());
        }
    }


    public Book getResult(){
        return this.book;
    }

}
