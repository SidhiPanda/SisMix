package com.sismics.books.core.model.jpa;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.google.common.base.Objects;
import com.sismics.books.core.constant.Constants;

/**
 * User audiobook entity.
 *
 * @author bgamard
 */
@Entity
@Table(name = "T_USER_AUDIOBOOK")
public class UserAudioBook implements Serializable {
    /**
     * Serial version UID.
     */
    private static final long serialVersionUID = 1L;


    /**
     * User audiobook ID.
     */
    @Id
    @Column(name = "UAB_ID_C", length = Constants.MAX_ID_LEN)
    private String id;

    /**
     * audiobook ID.
     */
    @Id
    @Column(name = "UAB_IDAB_C", nullable = false, length = Constants.MAX_ID_LEN)
    private String audioBookId;



    /**
     * User ID.
     */
    @Id
    @Column(name = "UAB_IDUSER_C", nullable = false, length = Constants.MAX_ID_LEN)
    private String userId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAudioBookId() {
        return audioBookId;
    }

    public void setAudioBookId(String audioBookId) {
        this.audioBookId = audioBookId;
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
