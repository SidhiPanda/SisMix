package com.sismics.books.core.model.builder;

import com.sismics.books.core.constant.Constants;
import com.sismics.books.core.model.jpa.Podcast;
import org.codehaus.jackson.node.ObjectNode;

public class PodcastBuilder implements MediaBuilder {

    Podcast pod;


    public void createMedia() {
        this.pod = new Podcast();
    }


    public void setCoreContent(ObjectNode data) {

        String id = data.get("id").getTextValue();
        if (id != null && id.length() <= Constants.MAX_ID_LEN) {
            this.pod.setId(id);
        }

        String title = data.get("title").getTextValue();
        if (title != null && title.length() <= Constants.MAX_TITLE_LEN) {
            this.pod.setTitle(title);
        }

        String host = data.get("host").getTextValue();
        if (host != null && host.length() <= Constants.MAX_TITLE_LEN) {
            this.pod.setHost(host);
        }
    }

    public void setMediaProfile(ObjectNode data) {
//        this.pod.setDescription(data.get("description").getTextValue());
//        this.pod.setExplicit(data.get("explicit").getTextValue());
//        this.pod.setGenre(data.get("genre").getTextValue());

        String description = data.get("description").getTextValue();
        if (description != null && description.length() <= Constants.MAX_DESCRIPTION_LEN) {
            this.pod.setDescription(description);
        }

        String explicit = data.get("explicit").getTextValue();
        if (explicit != null && explicit.length() <= Constants.MAX_GENRE_LEN) {
            this.pod.setExplicit(explicit);
        }

        String genre = data.get("genre").getTextValue();
        if (genre != null && genre.length() <= Constants.MAX_GENRE_LEN) {
            this.pod.setGenre(genre);
        }
    }

    public void setMetaData(ObjectNode data) {
//        this.pod.setLanguage(data.get("language").getTextValue());
//        this.pod.setCoverUrl(data.get("coverUrl").getTextValue());
//        this.pod.setReleaseDate(data.get("releaseDate").getTextValue());
//        this.pod.setTrackCount(data.get("totalTracks").getIntValue());
//        this.pod.setHostId(data.get("hostId").getTextValue());

        String language = data.get("language").getTextValue();
        if (language != null && language.length() <= Constants.MAX_GENRE_LEN) {
            this.pod.setLanguage(language);
        }

        String coverUrl = data.get("coverUrl").getTextValue();
        if (coverUrl != null && coverUrl.length() <= Constants.MAX_STRING_LEN) {
            this.pod.setCoverUrl(coverUrl);
        }

        String releaseDate = data.get("releaseDate").getTextValue();
        if (releaseDate != null && releaseDate.length() <= Constants.MAX_GENRE_LEN) {
            this.pod.setReleaseDate(releaseDate);
        }

        int trackCount = data.get("totalTracks").getIntValue();
        this.pod.setTrackCount(trackCount);

        String hostId = data.get("hostId").getTextValue();
        if (hostId != null && hostId.length() <= Constants.MAX_AUTHOR_ID) {
            this.pod.setHostId(hostId);
        }
    }

    public Podcast getResult(){
        return this.pod;
    }

}
