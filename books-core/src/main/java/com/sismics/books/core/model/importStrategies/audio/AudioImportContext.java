package com.sismics.books.core.model.importStrategies.audio;

import org.codehaus.jackson.node.ObjectNode;

import java.util.ArrayList;

public class AudioImportContext {

    private AudioImportStrategy strategy;

    public void setStrategy(AudioImportStrategy strategy) {
        this.strategy = strategy;
    }

    public ArrayList<ObjectNode> execute(String type, String searchField, String query) throws Exception {
        return this.strategy.pullData(type, searchField, query);
    }
}
