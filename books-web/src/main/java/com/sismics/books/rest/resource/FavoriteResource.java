package com.sismics.books.rest.resource;

import com.sismics.books.core.dao.jpa.AudioBookDao;
import com.sismics.books.core.dao.jpa.PodcastDao;
import com.sismics.books.core.dao.jpa.UserAudioBookDao;
import com.sismics.books.core.dao.jpa.UserPodcastDao;
import com.sismics.books.core.model.jpa.AudioBook;
import com.sismics.books.core.model.jpa.Podcast;
import com.sismics.books.core.model.jpa.UserAudioBook;
import com.sismics.books.core.model.jpa.UserPodcast;
import com.sismics.rest.exception.ForbiddenClientException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jettison.json.JSONObject;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/favorites")
public class FavoriteResource extends BaseResource{

    @GET
    @Path("show")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAudioFavorite(@QueryParam("audioType") String audioType
                                        ) throws Exception{

        if (!authenticate()) {
            throw new ForbiddenClientException();
        }

        JSONObject response = new JSONObject();

        if(audioType.equalsIgnoreCase("podcast")){
            UserPodcastDao userPodcastDao = new UserPodcastDao();
            List<Podcast> pdList = userPodcastDao.getUserPodcast(principal.getId());

            response.put("type","podcast");
            response.put("audioFav",pdList);
        }
        else if(audioType.equalsIgnoreCase("audiobook")){
            UserAudioBookDao userAudioBookDao = new UserAudioBookDao();
            List<AudioBook> abList = userAudioBookDao.getUserAudioBook(principal.getId());

            response.put("type","audiobook");
            response.put("audioFav", abList);
        }

        return Response.ok(response).build();

    }

    @GET
    @Path("add")
    @Produces(MediaType.APPLICATION_JSON)
    public Response addFav(@QueryParam("audioType") String audioType,
                           @QueryParam("audioObj") String audioObj
                            ) throws Exception{

        if (!authenticate()) {
            throw new ForbiddenClientException();
        }

        JSONObject response = new JSONObject();
        ObjectMapper objectMapper = new ObjectMapper();

        if(audioType.equalsIgnoreCase("audiobook")){
            AudioBook audioBook = objectMapper.readValue(audioObj, AudioBook.class);

            // creating the userAudioBook
            UserAudioBook userAudioBook = new UserAudioBook();
            userAudioBook.setUserId(principal.getId());
            userAudioBook.setAudioBookId(audioBook.getId());

            // saving the userAudioBook
            UserAudioBookDao userAudioBookDao = new UserAudioBookDao();
            userAudioBookDao.create(userAudioBook);

            // saving the audiobook
            AudioBookDao audioBookDao = new AudioBookDao();
            audioBookDao.create(audioBook);


        }
        else if(audioType.equalsIgnoreCase("podcast")){
            Podcast podcast = objectMapper.readValue(audioObj, Podcast.class);

            // creating the userPodcast
            UserPodcast userPodcast = new UserPodcast();
            userPodcast.setPodcastId(podcast.getId());
            userPodcast.setUserId(principal.getId());

            // saving the userPodcast
            try {
                UserPodcastDao userPodcastDao = new UserPodcastDao();
                userPodcastDao.create(userPodcast);
            }
            catch (Exception e) {
                System.out.println("problems :((((((((((((((");
                throw new Exception();
            }

            //saving the podcast
            try {
                PodcastDao podcastDao = new PodcastDao();
                podcastDao.create(podcast);
            }
            catch (Exception e) {
                System.out.println("problems :((((((((((((((");
                throw new Exception();
            }


        }

        response.put("stat","OK");

        return Response.ok(response).build();
    }

    @GET
    @Path("remove")
    @Produces(MediaType.APPLICATION_JSON)
    public Response removeFav(@QueryParam("audioType") String audioType,
                              @QueryParam("audioId") String audioId
                            ) throws Exception{
        if (!authenticate()) {
            throw new ForbiddenClientException();
        }

        JSONObject response = new JSONObject();

        if(audioType.equalsIgnoreCase("audiobook")){
            UserAudioBookDao userAudioBookDao = new UserAudioBookDao();
            userAudioBookDao.delete(principal.getId(),audioId);
        }
        else if(audioType.equalsIgnoreCase("podcast")){
            UserPodcastDao userPodcastDao = new UserPodcastDao();

            userPodcastDao.delete(principal.getId(),audioId);
        }

        response.put("stat","OK");

        return Response.ok(response).build();
    }


}
