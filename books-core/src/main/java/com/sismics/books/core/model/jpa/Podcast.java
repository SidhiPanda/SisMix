package com.sismics.books.core.model.jpa;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.google.common.base.Objects;
import com.restfb.json.JsonObject;
import com.restfb.json.JsonStringer;
import netscape.javascript.JSObject;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

/**
 * Podcast entity.
 *
 * @author bgamard
 */
@Entity
@Table(name = "T_PODCAST")
public class Podcast {

    /**
     * Podcast ID.
     */
    @Id
    @Column(name = "POD_ID_C", length = 36)
    private String id;

    /**
     * Title.
     */
    @Column(name = "POD_TITLE_C", nullable = false, length = 255)
    private String title;

    /**
     * Host.
     */
    @Column(name = "POD_HOST_C", nullable = false, length = 255)
    private String host;


    /**
     * host id
     */
    @Column(name = "POD_HOST_ID_C", length = 128)
    private String hostId;


    /**
     * Description.
     */
    @Column(name = "POD_DESCRIPTION_C", length = 4000)
    private String description;

    /**
     * Explicit Content.
     */
    @Column(name = "POD_EXP_C", length = 100)
    private String explicit;

    /**
     * genre.
     */
    @Column(name = "POD_GEN_C", length = 100)
    private String genre;


    /**
     * Language (ISO 639-1).
     */
    @Column(name = "POD_LANGUAGE_C", length = 100)
    private String language;

    /**
     * Release date.
     */
    @Column(name = "POD_RELEASEDATE_D", length = 100)
    private String releaseDate;

    /**
     * Track Count.
     */
    @Column(name = "POD_TRACKS_C")
    private int trackCount;

    /**
     * thumbnail url.
     */
    @Column(name = "POD_COVERURl_C", length = 2048)
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

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getHostId() {
        return hostId;
    }

    public void setHostId(String hostId) {
        this.hostId = hostId;
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

    public int getTrackCount() {
        return trackCount;
    }

    public void setTrackCount(int trackCount) {
        this.trackCount = trackCount;
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
//                .add("host", host)
//                .add("hostId", hostId)
//                .add("description", description)
//                .add("explicit", explicit)
//                .add("genre", genre)
//                .add("language", language)
//                .add("releaseDate", releaseDate)
//                .add("trackCount", trackCount)
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
                .key("host")
                .value(host)
                .key("hostId")
                .value(hostId)
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
                .key("trackCount")
                .value(trackCount)
                .key("coverUrl")
                .value(coverUrl)
                .endObject()
                .toString();
    }

}
