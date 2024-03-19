package com.sismics.books.rest.strategy.strategies;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.util.List;

public interface RankingStrategy {
    public List<JSONObject> getRankedBooks(String arg, String userId) throws JSONException;
}
