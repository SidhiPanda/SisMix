package com.sismics.books.core.model.jpa;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.google.common.base.Objects;
import com.restfb.json.JsonStringer;

/**
 * AudioBook entity.
 *
 * @author bgamard
 */
@Entity
@Table(name = "T_AUDIOBOOK")
public class AudioBook {
    /**
     * AudioBook ID.
     */
    @Id
    @Column(name = "AUDBOK_ID_C", length = 36)
    private String id;

    /**
     * Title.
     */
    @Column(name = "AUDBOK_TITLE_C", nullable = false, length = 255)
    private String title;

    /**
     * Author.
     */
    @Column(name = "AUDBOK_AUTHOR_C", nullable = false, length = 255)
    private String author;


    /**
     * author id
     */
    @Column(name = "AUDBOK_AUTHOR_ID_C", length = 128)
    private String authorId;

    /**
     * Narrator.
     */
    @Column(name = "AUDBOK_NARRATOR_C", length = 255)
    private String narrator;


    /**
     * Description.
     */
    @Column(name = "AUDBOK_DESCRIPTION_C", length = 4000)
    private String description;

    /**
     * Explicit Content.
     */
    @Column(name = "AUDBOK_EXP_C", length = 100)
    private String explicit;

    /**
     * genre.
     */
    @Column(name = "AUDBOK_GEN_C", length = 100)
    private String genre;


    /**
     * Language (ISO 639-1).
     */
    @Column(name = "AUDBOK_LANGUAGE_C", length = 100)
    private String language;

    /**
     * Release date.
     */
    @Column(name = "AUDBOK_RELEASEDATE_D", length = 100)
    private String releaseDate;




    /**
     * thumbnail url.
     */
    @Column(name = "AUDBOK_COVERURl_C", length = 2048)
    private String coverUrl;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public String getNarrator() {
        return narrator;
    }

    public void setNarrator(String narrator) {
        this.narrator = narrator;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getExplicit() {
        return explicit;
    }

    public void setExplicit(String explicit) {
        this.explicit = explicit;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

//    @Override
//    public String toString() {
//        return Objects.toStringHelper(this)
//                .add("id", id)
//                .add("title", title)
//                .add("author", author)
//                .add("authorId", authorId)
//                .add("narrator", narrator)
//                .add("description", description)
//                .add("explicit", explicit)
//                .add("genre", genre)
//                .add("language", language)
//                .add("releaseDate", releaseDate)
//                .add("coverUrl", coverUrl)
//                .toString();
//    }

    @Override
    public String toString() {
        return new JsonStringer()
                .object()
                .key("id")
                .value(id)
                .key("title")
                .value(title)
                .key("author")
                .value(author)
                .key("authorId")
                .value(authorId)
                .key("narrator")
                .value(narrator)
                .key("description")
                .value(description)
                .key("explicit")
                .value(explicit)
                .key("genre")
                .value(genre)
                .key("language")
                .value(language)
                .key("releaseDate")
                .value(releaseDate)
                .key("coverUrl")
                .value(coverUrl)
                .endObject()
                .toString();
    }
}
