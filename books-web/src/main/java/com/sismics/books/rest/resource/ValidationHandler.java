package com.sismics.books.rest.resource;

import com.sismics.books.core.constant.Constants;
import com.sismics.books.core.model.jpa.User;
import com.sismics.rest.util.ValidationUtil;

public class ValidationHandler implements RegisterHandler {
    private RegisterHandler nextHandler;

    /**
     * Sets a pointer to the next handler.
     * @param nextHandler
     */
    public void setNextHandler(RegisterHandler nextHandler) {
        this.nextHandler = nextHandler;
    }

    /**
     * Validation request handler
     * @param user
     * @throws Exception
     */
    public void handleRequest(User user) throws Exception {
        String username = ValidationUtil.validateLength(user.getUsername(), "username", Constants.MIN_USERNAME_LEN, Constants.MAX_USERNAME_LEN);
        ValidationUtil.validateAlphanumeric(username, "username");
        String password = ValidationUtil.validateLength(user.getPassword(), "password", Constants.MIN_PWD_LEN, Constants.MAX_PWD_LEN);
        String email = ValidationUtil.validateLength(user.getEmail(), "email", Constants.MIN_EMAIL_LEN, Constants.MAX_EMAIL_LEN);
        ValidationUtil.validateEmail(email, "email");

        if (nextHandler != null) {
            nextHandler.handleRequest(user);
        }
    }
}
