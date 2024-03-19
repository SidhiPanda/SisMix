package com.sismics.books.core.model.jpa;

import java.io.Serializable;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.google.common.base.Objects;
import com.sismics.books.core.constant.Constants;

/**
 * User podcast entity.
 *
 * @author bgamard
 */
@Entity
@Table(name = "T_USER_PODCAST")
public class UserPodcast implements Serializable {
    /**
     * Serial version UID.
     */
    private static final long serialVersionUID = 1L;


    /**
     * User podcast ID.
     */
    @Id
    @Column(name = "UPOD_ID_C", length = Constants.MAX_ID_LEN)
    private String id;

    /**
     * podcast ID.
     */
    @Id
    @Column(name = "UPOD_IDPOD_C", nullable = false, length = Constants.MAX_ID_LEN)
    private String podcastId;

    /**
     * User ID.
     */
    @Id
    @Column(name = "UPOD_IDUSER_C", nullable = false, length = Constants.MAX_ID_LEN)
    private String userId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPodcastId() {
        return podcastId;
    }

    public void setPodcastId(String podcastId) {
        this.podcastId = podcastId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("id", id)
                .toString();
    }
}
