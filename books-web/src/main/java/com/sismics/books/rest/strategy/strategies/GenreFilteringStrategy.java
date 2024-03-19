package com.sismics.books.rest.strategy.strategies;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.sismics.books.core.dao.jpa.LibraryBookDao;
import com.sismics.books.core.model.jpa.LibraryBook;

import com.sismics.books.rest.util.StringUtil;

public class GenreFilteringStrategy implements FilteringStrategy {
    
    @Override
    public List<JSONObject> getFilteredBooks(String arg, String userId) throws JSONException {
        List<JSONObject> books = new ArrayList<>();

        // split arg by comma
        List<String> searchParams = StringUtil.StringToList(arg, "\\|");
        
        LibraryBookDao libraryBookDao = new LibraryBookDao();
        List<LibraryBook> libraryBooks = libraryBookDao.findAll();


        HashSet<LibraryBook> libraryBookSet= new HashSet<LibraryBook>();

        for (LibraryBook libraryBook : libraryBooks) {
            List<String> genres = StringUtil.StringToList(libraryBook.getGenres(), "\\|");
            for (String genre : genres) {
                for (String searchParam : searchParams) {
                    if (genre.equalsIgnoreCase(searchParam)) {
                        libraryBookSet.add(libraryBook);
                    }
                }
                List<String> subGenres = StringUtil.StringToList(genre, ",");
                for (String subGenre : subGenres) {
                    for (String searchParam : searchParams) {
                        if (subGenre.equalsIgnoreCase(searchParam)) {
                            libraryBookSet.add(libraryBook);
                        }
                    }
                }
            }
        }

        for (LibraryBook libraryBook : libraryBookSet) {
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
