package com.sismics.books.core.dao.jpa;

import com.sismics.books.core.model.jpa.LibraryBook;
import com.sismics.books.core.model.jpa.LibraryBookRating;
import com.sismics.util.context.ThreadLocalContext;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import java.util.List;

public class LibraryBookDao {

    public String create(LibraryBook libraryBook) {
        // Create the library book
        EntityManager em = ThreadLocalContext.get().getEntityManager();
        em.persist(libraryBook);
        
        return libraryBook.getId();
    }

    public LibraryBook getById(String id) {
        EntityManager em = ThreadLocalContext.get().getEntityManager();
        try {
            return em.find(LibraryBook.class, id);
        } catch (NoResultException e) {
            System.err.println("NoResultException: " + e.getMessage());
            return null;
        }
    }
    
    @SuppressWarnings("unchecked")
    public List<LibraryBook> findAll() {
        EntityManager em = ThreadLocalContext.get().getEntityManager();
        Query q = em.createQuery("select l from LibraryBook l order by l.id");
        return q.getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<LibraryBook> findByCriteria(String customQuery, String param) {
        // check if customQuery contains "rating"
        EntityManager em = ThreadLocalContext.get().getEntityManager();
        Query q = em.createQuery(customQuery);
        if (customQuery.contains("rating >= :param") || customQuery.contains("rating <= :param")) {
            float paramFloat = 0;
            paramFloat = Float.parseFloat(param);
            q.setParameter("param", paramFloat);
        }
        else if (param.equals("") && !customQuery.contains(":")) {
            // do nothing
            q.setMaxResults(10);
        }
        else
            q.setParameter("param", param);
        return q.getResultList();
    }

    public float getBookRating(String bookId) {
        EntityManager em = ThreadLocalContext.get().getEntityManager();
        Query q = em.createQuery("select l.rating from LibraryBook l where l.id = :bookId");
        q.setParameter("bookId", bookId);
        try {
            return (Float) q.getSingleResult();
        } catch (NoResultException e) {
            return 0;
        }
    }

    public void updateBookRating(String bookId, float rating, boolean isANewRating) {
        EntityManager em = ThreadLocalContext.get().getEntityManager();
        Query q = em.createQuery("update LibraryBook l set l.rating = :rating where l.id = :bookId");
        q.setParameter("rating", rating);
        q.setParameter("bookId", bookId);
        try {
            q.executeUpdate();
        } catch (Exception e) {
            System.err.println("NoResultException: " + e.getMessage() + " when updating rating");
        }

        if(isANewRating) {
            Query q2 = em.createQuery("update LibraryBook l set l.numRatings = l.numRatings + 1 where l.id = :bookId");
            q2.setParameter("bookId", bookId);
            try {
                q2.executeUpdate();
            } catch (NoResultException e) {
                System.err.println("NoResultException: " + e.getMessage() + " when updating numRatings");
            }
        }
    }

    public float findUserRating(String bookId, String userId) {
        EntityManager em = ThreadLocalContext.get().getEntityManager();
        Query q = em.createQuery("select lr.rating from LibraryBookRating lr where lr.libraryBookId = :bookId and lr.userId = :userId");
        q.setParameter("bookId", bookId);
        q.setParameter("userId", userId);
        try {
            return (Float) q.getSingleResult();
        } catch (NoResultException e) {
            return -1.0f;
        }
    }

    public void setUserRating(String bookId, String userId, float rating) {
        EntityManager em = ThreadLocalContext.get().getEntityManager();
        Query q = em.createQuery("select lr from LibraryBookRating lr where lr.libraryBookId = :bookId and lr.userId = :userId");
        q.setParameter("bookId", bookId);
        q.setParameter("userId", userId);
        try {
            q.getSingleResult();
            Query q2 = em.createQuery("update LibraryBookRating lr set lr.rating = :rating where lr.libraryBookId = :bookId and lr.userId = :userId");
            q2.setParameter("rating", rating);
            q2.setParameter("bookId", bookId);
            q2.setParameter("userId", userId);
            q2.executeUpdate();
        } catch (NoResultException e) {
            LibraryBookRating lr = new LibraryBookRating(bookId, userId, rating);
            em.persist(lr);
        }
    }
}
