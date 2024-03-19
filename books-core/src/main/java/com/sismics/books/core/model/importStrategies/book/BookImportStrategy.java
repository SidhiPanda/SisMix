package com.sismics.books.core.model.importStrategies.book;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ObjectNode;

public interface BookImportStrategy {

    public ObjectNode pullData(String isbn) throws Exception;
}
