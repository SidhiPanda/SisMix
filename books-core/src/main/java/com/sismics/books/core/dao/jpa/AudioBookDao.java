package com.sismics.books.core.dao.jpa;

import com.sismics.books.core.model.jpa.AudioBook;
import com.sismics.util.context.ThreadLocalContext;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

public class AudioBookDao {

    public String create(AudioBook audioBook) throws Exception{
        // Create the book
        EntityManager em = ThreadLocalContext.get().getEntityManager();
        try {
            em.persist(audioBook);
        }
        catch (Exception e){
            throw new Exception();
        }
        return audioBook.getId();
    }

    /**
     * Gets a podcast by its ID.
     *
     *
     *
     */
    public AudioBook getById(String id) {
        EntityManager em = ThreadLocalContext.get().getEntityManager();
        try {
            return em.find(AudioBook.class, id);
        } catch (NoResultException e) {
            return null;
        }
    }
}
