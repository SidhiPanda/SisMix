package com.sismics.books.rest.strategy.strategies;

import java.util.List;

import org.codehaus.jettison.json.JSONObject;

public interface FilteringStrategy {
    public List<JSONObject> getFilteredBooks(String arg, String userId) throws Exception;
}
