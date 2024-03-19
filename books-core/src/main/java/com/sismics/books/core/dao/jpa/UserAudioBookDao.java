package com.sismics.books.core.dao.jpa;

import com.sismics.books.core.interfaces.Dao;
import com.sismics.books.core.model.jpa.AudioBook;

import com.sismics.books.core.model.jpa.UserAudioBook;

import com.sismics.util.context.ThreadLocalContext;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.UUID;

public class UserAudioBookDao implements Dao<UserAudioBook> {



    public String create(UserAudioBook userAudioBook) throws Exception {
        // Create the UUID
        userAudioBook.setId(UUID.randomUUID().toString());

        // Create the user book
        EntityManager em = ThreadLocalContext.get().getEntityManager();
        try {
            em.persist(userAudioBook);
        }
        catch (Exception e){
            throw new Exception();
        }

        return userAudioBook.getId();
    }

    public ArrayList<AudioBook> getUserAudioBook(String userId) {
        EntityManager em = ThreadLocalContext.get().getEntityManager();
        Query q = em.createQuery("select uab from UserAudioBook uab where uab.userId = :userId");
        q.setParameter("userId", userId);
        ArrayList<UserAudioBook> uabList= new ArrayList<UserAudioBook>();
        try {
            uabList = (ArrayList<UserAudioBook>) q.getResultList();
        } catch (NoResultException e) {
            return null;
        }

        ArrayList<AudioBook> abList = new ArrayList<AudioBook>();
        AudioBookDao audioBookDao = new AudioBookDao();
        for(UserAudioBook userAudioBook: uabList){
           abList.add(audioBookDao.getById(userAudioBook.getAudioBookId()));
        }

        return abList;
    }


    public void delete(String id) throws Exception {
        EntityManager em = ThreadLocalContext.get().getEntityManager();
        em.getTransaction().begin();


        Query q = em.createQuery("delete from UserAudioBook uab WHERE uab.id = :id");
        q.setParameter("id", id);
        int deletedCount = q.executeUpdate();

        em.getTransaction().commit();
        em.close();

        if (deletedCount == 0) {
            throw new Exception("Audiobook with ID " + id + " not found");
        }

    }

    public void delete(String userId, String audioBookId) throws Exception {
        EntityManager em = ThreadLocalContext.get().getEntityManager();


        Query q = em.createQuery("delete from UserAudioBook uab WHERE uab.audioBookId = :abid and uab.userId = :uid");
        q.setParameter("abid", audioBookId);
        q.setParameter("uid",userId);
        int deletedCount = q.executeUpdate();


        if (deletedCount == 0) {
            throw new Exception("Audiobook with ID " + audioBookId + " not found");
        }

    }
}