package com.sismics.books.rest.resource;

import com.sismics.books.core.model.jpa.User;

public interface RegisterHandler {
    /**
     * Sets a pointer to the next handler
     * @param nextHandler
     */
    void setNextHandler(RegisterHandler nextHandler);

    /**
     * Request handler
     * @param user
     * @throws Exception
     */
    void handleRequest(User user) throws Exception;
}
