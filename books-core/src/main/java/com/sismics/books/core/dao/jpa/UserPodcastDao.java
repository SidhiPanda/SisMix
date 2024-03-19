package com.sismics.books.core.dao.jpa;

import com.sismics.books.core.interfaces.Dao;
import com.sismics.books.core.model.jpa.Podcast;

import com.sismics.books.core.model.jpa.UserPodcast;
import com.sismics.util.context.ThreadLocalContext;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.util.ArrayList;

import java.util.UUID;

public class UserPodcastDao implements Dao<UserPodcast> {


    public String create(UserPodcast userPodcast) throws Exception {
        // Create the UUID
        userPodcast.setId(UUID.randomUUID().toString());


        // Create the user book
        EntityManager em = ThreadLocalContext.get().getEntityManager();
        try {
            em.persist(userPodcast);
        }
        catch (Exception e){
            throw new Exception();
        }

        return userPodcast.getId();
    }

    public ArrayList<Podcast> getUserPodcast(String userId) {
        EntityManager em = ThreadLocalContext.get().getEntityManager();
        Query q = em.createQuery("select upd from UserPodcast upd where upd.userId = :userId");
        q.setParameter("userId", userId);
        ArrayList<UserPodcast> upList= new ArrayList<UserPodcast>();
        try {
            upList = (ArrayList<UserPodcast>) q.getResultList();
        } catch (NoResultException e) {
            return null;
        }

        ArrayList<Podcast> pdList = new ArrayList<Podcast>();
        PodcastDao podcastDao = new PodcastDao();
        for(UserPodcast userPodcast: upList){
            pdList.add(podcastDao.getById(userPodcast.getPodcastId()));
        }

        return pdList;
    }


    public void delete(String id) throws Exception {
        EntityManager em = ThreadLocalContext.get().getEntityManager();
        em.getTransaction().begin();

        // Find the T_USER_PODCAST entry with the specified id
        Query q = em.createQuery("delete from UserPodcast upod WHERE upod.id = :id");
        q.setParameter("id", id);
        int deletedCount = q.executeUpdate();

        em.getTransaction().commit();
        em.close();

        if (deletedCount == 0) {
            throw new Exception("Podcast with ID " + id + " not found");
        }

    }

    public void delete(String userId, String podcastId) throws Exception {
        EntityManager em = ThreadLocalContext.get().getEntityManager();


        Query q = em.createQuery("delete from UserPodcast upod WHERE upod.podcastId = :pdid and upod.userId = :uid");
        q.setParameter("pdid", podcastId);
        q.setParameter("uid",userId);
        int deletedCount = q.executeUpdate();

        if (deletedCount == 0) {
            throw new Exception("Podcast with ID " + podcastId + " not found");
        }

    }
}
