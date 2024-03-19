package com.sismics.books.rest.resource;

import com.sismics.books.core.model.jpa.User;
import com.sismics.util.context.ThreadLocalContext;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;

public class UnicityCheckHandler implements RegisterHandler {
    private RegisterHandler nextHandler;

    /**
     * Sets a pointer to the next handler
     * @param nextHandler
     */
    public void setNextHandler(RegisterHandler nextHandler) {
        this.nextHandler = nextHandler;
    }

    /**
     * Unicity check request handler
     * @param user
     * @throws Exception
     */
    public void handleRequest(User user) throws Exception {
        EntityManager em = ThreadLocalContext.get().getEntityManager();

        Query q = em.createQuery("select u from User u where u.email = :email and u.deleteDate is null");
        q.setParameter("email", user.getEmail());
        List<?> emailResult = q.getResultList();
        if (emailResult.size() > 0) {
            throw new Exception("AlreadyExistingEmail");
        }

        q = em.createQuery("select u from User u where u.username = :username and u.deleteDate is null");
        q.setParameter("username", user.getUsername());
        List<?> usernameResult = q.getResultList();
        if (usernameResult.size() > 0) {
            throw new Exception("AlreadyExistingUsername");
        }

        if (nextHandler != null) {
            nextHandler.handleRequest(user);
        }
    }
}
