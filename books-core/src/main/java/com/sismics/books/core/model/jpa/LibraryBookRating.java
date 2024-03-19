package com.sismics.books.core.model.jpa;

import java.io.Serializable;

import javax.persistence.*;

import com.sismics.books.core.constant.Constants;

@Entity
@Table(name="L_BOOK_RATING")
public class LibraryBookRating implements Serializable {

    @Id
    @Column(name="L_BOOK_R_ID", length = Constants.DEFAULT_ID_LEN)
    private String id;

    @Column(name="L_BOOK_ID", length = Constants.DEFAULT_ID_LEN)
    private String libraryBookId;

    @Column(name="L_USER_ID", length = Constants.DEFAULT_ID_LEN)
    private String userId;

    @Column(name="L_BOOK_R")
    private float rating;

    public LibraryBookRating(String libraryBookId, String userId, float rating) {
        this.id = java.util.UUID.randomUUID().toString();
        this.libraryBookId = libraryBookId;
        this.userId = userId;
        this.rating = rating;
    }

    public LibraryBookRating() {
    }

    public void setLibraryBookId(String libraryBookId) {
        this.libraryBookId = libraryBookId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getLibraryBookId() {
        return libraryBookId;
    }

    public String getUserId() {
        return userId;
    }

    public float getRating() {
        return rating;
    }
}
