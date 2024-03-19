package com.sismics.books.rest.strategy.strategies;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.sismics.books.core.dao.jpa.LibraryBookDao;
import com.sismics.books.core.model.jpa.LibraryBook;


public class DefaultFilteringStrategy implements FilteringStrategy {

    public List<JSONObject> getFilteredBooks(String arg, String userId) throws JSONException {
        List<JSONObject> books = new ArrayList<>();
        
        LibraryBookDao libraryBookDao = new LibraryBookDao();
        List<LibraryBook> libraryBooks = libraryBookDao.findAll();
        for (LibraryBook libraryBook : libraryBooks) {
            JSONObject book = new JSONObject();
            book.put("id", libraryBook.getId());
            book.put("title", libraryBook.getTitle());
            book.put("authors", libraryBook.getAuthors());
            book.put("genres", libraryBook.getGenres());
            book.put("rating", libraryBook.getRating());
            book.put("thumbURL", libraryBook.getThumbURL());
            book.put("numRatings", libraryBook.getNumRatings());
            book.put("userRating", libraryBookDao.findUserRating(libraryBook.getId(), userId));
            books.add(book);
        }

        return books;
    }
}
