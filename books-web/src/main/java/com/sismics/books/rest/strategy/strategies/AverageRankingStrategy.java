package com.sismics.books.rest.strategy.strategies;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.sismics.books.core.dao.jpa.LibraryBookDao;
import com.sismics.books.core.model.jpa.LibraryBook;

import java.util.ArrayList;
import java.util.List;

public class AverageRankingStrategy implements RankingStrategy {
    
    @Override
    public List<JSONObject> getRankedBooks(String arg, String userId) throws JSONException {
        String customQuery = "select l from LibraryBook l order by l.rating desc";
        LibraryBookDao libraryBookDao = new LibraryBookDao();
        List<LibraryBook> libraryBooks = libraryBookDao.findByCriteria(customQuery, "");

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
