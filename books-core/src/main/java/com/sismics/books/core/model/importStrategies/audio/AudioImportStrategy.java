package com.sismics.books.core.model.importStrategies.audio;

import org.codehaus.jackson.node.ObjectNode;

import java.util.ArrayList;

public interface AudioImportStrategy {

    public ArrayList<ObjectNode> pullData(String type, String searchField, String query) throws Exception;
}
