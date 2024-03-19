package com.sismics.books.rest.command;

import org.codehaus.jettison.json.JSONObject;

import java.util.List;

public interface LibraryCommand {
    public List<JSONObject> getLibraryBooks(String type, String arg, String userId) throws Exception;
}
