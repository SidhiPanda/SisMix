package com.sismics.books.rest.resource;

import com.sismics.books.core.model.builder.AudiobookBuilder;
import com.sismics.books.core.model.builder.BookBuilder;
import com.sismics.books.core.model.builder.PodcastBuilder;
import com.sismics.books.core.model.importStrategies.audio.AudioImportContext;
import com.sismics.books.core.model.importStrategies.audio.ItunesImport;
import com.sismics.books.core.model.importStrategies.audio.SpotifyImport;
import com.sismics.books.core.model.importStrategies.book.BookImportContext;
import com.sismics.books.core.model.importStrategies.book.GoogleImport;
import com.sismics.books.core.model.importStrategies.book.OpenLibraryImport;
import com.sismics.books.core.model.jpa.AudioBook;
import com.sismics.books.core.model.jpa.Book;
import com.sismics.books.core.model.jpa.Podcast;
import com.sismics.rest.exception.ForbiddenClientException;
import org.codehaus.jackson.node.ObjectNode;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;


@Path("/mediaSearch")
public class CreateMediaResource extends BaseResource{

    private AudioImportContext audioImportContext;
    private BookImportContext bookImportContext;

    public CreateMediaResource() {
        this.audioImportContext = new AudioImportContext();
        this.bookImportContext = new BookImportContext();
    }

    private void setBookImportType(String importType){
        if(importType.equalsIgnoreCase("google")) {
            GoogleImport googleImport = new GoogleImport();
            this.bookImportContext.setStrategy(googleImport);
        }
        else if(importType.equalsIgnoreCase("openlibrary")) {
            OpenLibraryImport OLImport = new OpenLibraryImport();
            this.bookImportContext.setStrategy(OLImport);
        }
    }

    private void setAudioImportType(String importType){
        if(importType.equalsIgnoreCase("itunes")) {
            ItunesImport itunesImport = new ItunesImport();
            this.audioImportContext.setStrategy(itunesImport);
        }
        else if(importType.equalsIgnoreCase("spotify")) {
            SpotifyImport spotifyImport = new SpotifyImport();
            this.audioImportContext.setStrategy(spotifyImport);
        }
    }


    public Book createBook(String isbn, String importType) throws Exception{
        setBookImportType(importType);

        BookBuilder bookBuilder = new BookBuilder();
        bookBuilder.createMedia();

        ObjectNode data = this.bookImportContext.execute(isbn);
        bookBuilder.setCoreContent(data);
        bookBuilder.setMediaProfile(data);
        bookBuilder.setMetaData(data);

        return bookBuilder.getResult();
    }



    @GET
    @Path("audiobook")
    @Produces(MediaType.APPLICATION_JSON)
    public Response createAudioBook(@QueryParam("searchField") String searchField,
                                    @QueryParam("query") String query,
                                    @QueryParam("importType") String importType) throws Exception{
        if (!authenticate()) {
            throw new ForbiddenClientException();
        }

        JSONObject response = new JSONObject();

        setAudioImportType(importType);

        AudiobookBuilder audiobookBuilder = new AudiobookBuilder();

        ArrayList<ObjectNode> dataList = this.audioImportContext.execute("audiobook", searchField, query);
        List<AudioBook> audioBookList = new ArrayList<AudioBook>();

        for (ObjectNode data : dataList){
            audiobookBuilder.createMedia();
            audiobookBuilder.setCoreContent(data);
            audiobookBuilder.setMediaProfile(data);
            audiobookBuilder.setMetaData(data);

            audioBookList.add(audiobookBuilder.getResult());
        }

        response.put("type","audiobook");
        response.put("audio",audioBookList);

        return Response.ok(response).build();
    }


    @GET
    @Path("podcast")
    @Produces(MediaType.APPLICATION_JSON)
    public Response createPodcast(@QueryParam("searchField") String searchField,
                                  @QueryParam("query") String query,
                                  @QueryParam("importType") String importType) throws Exception{
        if (!authenticate()) {
            throw new ForbiddenClientException();
        }

        JSONObject response = new JSONObject();

        setAudioImportType(importType);

        PodcastBuilder podcastBuilder = new PodcastBuilder();

        ArrayList<ObjectNode> dataList = this.audioImportContext.execute("podcast", searchField, query);
        List<Podcast> podcastList = new ArrayList<Podcast>();

        for (ObjectNode data : dataList){
//            System.out.println(data);
            podcastBuilder.createMedia();
            podcastBuilder.setCoreContent(data);
            podcastBuilder.setMediaProfile(data);
            podcastBuilder.setMetaData(data);

            podcastList.add(podcastBuilder.getResult());
        }

        response.put("type","podcast");
        response.put("audio",podcastList);

        return Response.ok(response).build();
    }

}
