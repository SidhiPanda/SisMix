package com.sismics.books.core.dao.jpa;


import com.sismics.books.core.model.jpa.Podcast;
import com.sismics.util.context.ThreadLocalContext;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

public class PodcastDao{

    public String create(Podcast podcast) throws Exception{
        // Create the book
        EntityManager em = ThreadLocalContext.get().getEntityManager();
        try {
            em.persist(podcast);
        }
        catch (Exception e){
            throw new Exception();
        }
        return podcast.getId();
    }

    /**
     * Gets a podcast by its ID.
     *
     *
     *
     */
    public Podcast getById(String id) {
        EntityManager em = ThreadLocalContext.get().getEntityManager();
        try {
            return em.find(Podcast.class, id);
        } catch (NoResultException e) {
            return null;
        }
    }
}
