package com.sismics.books.core.model.jpa;

import com.sismics.books.core.constant.Constants;

import java.io.Serializable;

import javax.persistence.*;

/*
 *  Book for display in the Library
 */
@Entity
@Table(name="L_BOOK")
public class LibraryBook implements Serializable {
    @Id
    @Column(name="L_BOOK_ID", length = Constants.DEFAULT_ID_LEN)
    private String id;

    @Column(name="L_BOOK_T", length = Constants.MAX_TITLE_LEN)
    private String title;

    @Column(name = "L_BOOK_AUTH", length = Constants.MAX_STRING_LEN)
    private String authors;

    @Column(name = "L_BOOK_G", length = Constants.MAX_STRING_LEN)
    private String genres;

    @Column(name = "L_BOOK_R")
    private float rating;

    @Column(name = "L_BOOK_NR")
    private int numRatings;

    @Column(name = "L_BOOK_THUMB", length = Constants.MAX_URL_LEN)
    private String thumbURL;

    public LibraryBook(String id, String title, String authors, String genres, float rating, String thumbURL) {
        this.id = id;
        this.title = title;
        this.authors = authors;
        this.genres = genres;
        this.rating = rating;
        if (thumbURL == null) {
            this.thumbURL = "https://img.freepik.com/free-photo/book-composition-with-open-book_23-2147690555.jpg";
        } else {
            this.thumbURL = thumbURL;
        }
    }

    public LibraryBook () {

    }

    public void setId(String id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAuthors(String authors) {
        this.authors = authors;
    }

    public void setGenres(String genres) {
        this.genres = genres;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public void setThumbURL(String thumbURL) {
        this.thumbURL = thumbURL;
    }

    public void setNumRatings(int numRatings) {
        this.numRatings = numRatings;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthors() {
        return authors;
    }

    public String getGenres() {
        return genres;
    }

    public float getRating() {
        return rating;
    }

    public String getThumbURL() {
        return thumbURL;
    }

    public int getNumRatings() {
        return numRatings;
    }
}
