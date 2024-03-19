package com.sismics.books.rest.command;

import org.codehaus.jettison.json.JSONObject;

import com.sismics.books.rest.strategy.context.FilteringContext;
import com.sismics.books.rest.strategy.strategies.AuthorFilteringStrategy;
import com.sismics.books.rest.strategy.strategies.DefaultFilteringStrategy;
import com.sismics.books.rest.strategy.strategies.GenreFilteringStrategy;
import com.sismics.books.rest.strategy.strategies.RatingFilteringStrategy;

import java.util.List;

public class FilteringLibraryCommand implements LibraryCommand {
    
    @Override
    public List<JSONObject> getLibraryBooks(String type, String arg, String userId) throws Exception {
        FilteringContext filteringContext = new FilteringContext();
        switch (type) {
            case "default":
                filteringContext.setLibraryStrategy(new DefaultFilteringStrategy());
                break;
            case "rating":
                filteringContext.setLibraryStrategy(new RatingFilteringStrategy());
                break;
            case "genre":
                filteringContext.setLibraryStrategy(new GenreFilteringStrategy());
                break;
            case "author":
                filteringContext.setLibraryStrategy(new AuthorFilteringStrategy());
                break;
            default:
                filteringContext.setLibraryStrategy(new DefaultFilteringStrategy());
                break;
        }
        return filteringContext.getFilteredBooks(arg, userId);
    }
}
