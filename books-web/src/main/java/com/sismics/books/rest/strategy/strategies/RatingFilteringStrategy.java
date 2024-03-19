package com.sismics.books.rest.strategy.strategies;

import com.sismics.books.core.dao.jpa.LibraryBookDao;
import com.sismics.books.core.model.jpa.LibraryBook;

import org.codehaus.jettison.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class RatingFilteringStrategy implements FilteringStrategy {
    
    public List<JSONObject> getFilteredBooks(String arg, String userId) throws Exception {

        if (arg == null) {
            throw new Exception("Rating must not be null");
        }
        
        int rating = 0;
        // check if arg is an integer
        try {
            rating = Integer.parseInt(arg);
        } catch (NumberFormatException e) {
            throw new Exception("Rating must be an integer");
        }

        if (rating < 0 || rating > 10) {
            throw new Exception("Rating must be between 0 and 10");
        }

        // write query to get books with rating greater than or equal to arg
        String query = "select l from LibraryBook l where l.rating >= :param";

        LibraryBookDao libraryBookDao = new LibraryBookDao();
        List<LibraryBook> libraryBooks = libraryBookDao.findByCriteria(query, arg);

        List<JSONObject> books = new ArrayList<>();

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
