package com.sismics.books.rest.strategy.context;

import java.util.List;

import org.codehaus.jettison.json.JSONObject;

import com.sismics.books.rest.strategy.strategies.FilteringStrategy;

public class FilteringContext {
    private FilteringStrategy libraryStrategy;

    public FilteringContext () {
    }

    public void setLibraryStrategy(FilteringStrategy libraryStrategy) {
        this.libraryStrategy = libraryStrategy;
    }

    public FilteringStrategy getLibraryStrategy() {
        return libraryStrategy;
    }

    public List<JSONObject> getFilteredBooks(String arg, String userId) throws Exception {
        return libraryStrategy.getFilteredBooks(arg, userId);
    }
}
