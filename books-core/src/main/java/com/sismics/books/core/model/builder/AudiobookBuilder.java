package com.sismics.books.core.model.builder;

import com.sismics.books.core.constant.Constants;
import com.sismics.books.core.model.jpa.AudioBook;
import com.sun.org.apache.bcel.internal.Const;
import org.codehaus.jackson.node.ObjectNode;

public class AudiobookBuilder implements MediaBuilder{

    AudioBook audiobook;

    public void createMedia() {
        this.audiobook = new AudioBook();
    }


    public void setCoreContent(ObjectNode data) {
//        this.audiobook.setId(data.get("id").getTextValue());
//        this.audiobook.setTitle(data.get("title").getTextValue());
//        this.audiobook.setAuthor(data.get("author").getTextValue());

        String id = data.get("id").getTextValue();
        if (id != null && id.length() <= Constants.MAX_ID_LEN) {
            this.audiobook.setId(id);
        }

        String title = data.get("title").getTextValue();
        if (title != null && title.length() <= Constants.MAX_TITLE_LEN) {
            this.audiobook.setTitle(title);
        }

        String author = data.get("author").getTextValue();
        if (author != null && author.length() <= Constants.MAX_TITLE_LEN) {
            this.audiobook.setAuthor(author);
        }
    }


    public void setMediaProfile(ObjectNode data) {
//        this.audiobook.setDescription(data.get("description").getTextValue());
//        this.audiobook.setExplicit(data.get("explicit").getTextValue());
//        this.audiobook.setGenre(data.get("genre").getTextValue());

        String description = data.get("description").getTextValue();
        if (description != null && description.length() <= Constants.MAX_DESCRIPTION_LEN) {
            this.audiobook.setDescription(description);
        }

        String explicit = data.get("explicit").getTextValue();
        if (explicit != null && explicit.length() <= Constants.MAX_GENRE_LEN) {
            this.audiobook.setExplicit(explicit);
        }

        String genre = data.get("genre").getTextValue();
        if (genre != null && genre.length() <= Constants.MAX_GENRE_LEN) {
            this.audiobook.setGenre(genre);
        }
    }


    public void setMetaData(ObjectNode data) {
//        this.audiobook.setAuthorId(data.get("authorId").getTextValue());
//        this.audiobook.setNarrator(data.get("narrator").getTextValue());
//        this.audiobook.setLanguage(data.get("language").getTextValue());
//        this.audiobook.setReleaseDate(data.get("releaseDate").getTextValue());
//        this.audiobook.setCoverUrl(data.get("coverUrl").getTextValue());

        String authorId = data.get("authorId").getTextValue();
        if (authorId != null && authorId.length() <= Constants.MAX_AUTHOR_ID) {
            this.audiobook.setAuthorId(authorId);
        }

        String narrator = data.get("narrator").getTextValue();
        if (narrator != null && narrator.length() <= Constants.MAX_TITLE_LEN) {
            this.audiobook.setNarrator(narrator);
        }

        String language = data.get("language").getTextValue();
        if (language != null && language.length() <= Constants.MAX_GENRE_LEN) {
            this.audiobook.setLanguage(language);
        }

        String releaseDate = data.get("releaseDate").getTextValue();
        if (releaseDate != null && releaseDate.length() <= Constants.MAX_GENRE_LEN) {
            this.audiobook.setReleaseDate(releaseDate);
        }

        String coverUrl = data.get("coverUrl").getTextValue();
        if (coverUrl != null && coverUrl.length() <= Constants.MAX_STRING_LEN) {
            this.audiobook.setCoverUrl(coverUrl);
        }
    }


    public AudioBook getResult(){
        return this.audiobook;
    }
}
