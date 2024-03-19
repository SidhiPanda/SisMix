package com.sismics.books.core.model.importStrategies.book;

import org.codehaus.jackson.node.ObjectNode;

public class BookImportContext {
    BookImportStrategy strategy;

    public void setStrategy(BookImportStrategy strategy) {
        this.strategy = strategy;
    }

    public ObjectNode execute(String isbn) throws Exception {
        return this.strategy.pullData(isbn);
    }
}
