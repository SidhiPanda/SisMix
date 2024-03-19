package com.sismics.books.core.model.importStrategies.audio;

import com.google.common.util.concurrent.RateLimiter;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ItunesImport implements AudioImportStrategy{


    /**
     * Logger.
     */
    private static final Logger log = LoggerFactory.getLogger(ItunesImport.class);


    /**
     * itunes API Search URL.
     */
    private static final String itunes_api_search_endpoint = "https://itunes.apple.com/search?term=%s&media=%s&attribute=%s&limit=30";

    private static final int CONNECT_TIMEOUT = 10000;
    private static final int READ_TIMEOUT = 10000;

    /**
     * Executor for itunes API requests.
     */
    private ExecutorService executor;


    /**
     * Itunes API rate limiter.
     */
    private RateLimiter itunesRateLimiter = RateLimiter.create(20);


    protected void startUp() throws Exception {
        executor = Executors.newSingleThreadExecutor();

        if (log.isInfoEnabled()) {
            log.info("Audio import service started");
        }
    }

    private HashMap<String, String> validateSearchData(String type, String searchField, String query) throws Exception{
        if(!type.equalsIgnoreCase("audiobook") && !type.equalsIgnoreCase("podcast")){
            throw new Exception("audio type not valid");
        }

        if(!searchField.equalsIgnoreCase("host") && !searchField.equalsIgnoreCase("author") && !searchField.equalsIgnoreCase("title")){
            throw new Exception("Searching using requested field not supported");
        }

        HashMap<String, String> sd = new HashMap<>();

        sd.put("type",type);
        if(searchField.equalsIgnoreCase("host")){
            sd.put("searchField","artistTerm");
        }
        else if(searchField.equalsIgnoreCase("author")){
            sd.put("searchField","authorTerm");
        }
        else if(searchField.equalsIgnoreCase("title")){
            sd.put("searchField","titleTerm");
        }
        sd.put("query",query);

        return sd;
    }

    private ArrayList<ObjectNode> search(String type, String searchField, String query) throws Exception{
        itunesRateLimiter.acquire();

        InputStream inputStream = getStream(String.format(Locale.ENGLISH, itunes_api_search_endpoint, query, type, searchField));
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readValue(inputStream, JsonNode.class);

        if (rootNode.get("resultCount").getIntValue() <= 0) {
            throw new Exception("No audio found");
        }

        ArrayNode datalist = (ArrayNode) rootNode.get("results");
        ArrayList<ObjectNode> audio = new ArrayList<ObjectNode>();

        for (JsonNode jsonnode : datalist){
            if (type.equalsIgnoreCase("audiobook")) {
                audio.add(createAudioBookObject((ObjectNode) jsonnode));
            }
            else if(type.equalsIgnoreCase("podcast")){
                audio.add(createPodcastObject((ObjectNode) jsonnode));
            }
        }
        return audio;
    }

    private ObjectNode createAudioBookObject(ObjectNode data){
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode parseddata = mapper.createObjectNode();

        parseddata.put("id", data.has("collectionId") ? data.get("collectionId").getValueAsText() : "NA"); // returns a IntNode
        parseddata.put("title",data.has("collectionName") ? data.get("collectionName").getTextValue() : "NA");
        parseddata.put("author",data.has("artistName") ? data.get("artistName").getTextValue() : "NA");
        parseddata.put("authorId",data.has("artistId") ? data.get("artistId").getValueAsText() : "NA");
        parseddata.put("narrator", "NA");
        parseddata.put("explicit",data.has("collectionExplicitness") ? data.get("collectionExplicitness").getTextValue() : "NA");
        parseddata.put("description",data.has("description") ? data.get("description").getTextValue() : "NA");
        parseddata.put("genre",data.has("primaryGenreName") ? data.get("primaryGenreName").getTextValue() : "NA");
        parseddata.put("releaseDate",data.has("releaseDate") ? data.get("releaseDate").getTextValue() : "NA");
        parseddata.put("language","NA");
        parseddata.put("coverUrl",data.has("artworkUrl100") ? data.get("artworkUrl100").getTextValue() : "NA");

        return parseddata;
    }

    private ObjectNode createPodcastObject(ObjectNode data){
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode parseddata = mapper.createObjectNode();


//        System.out.println("collectionId: " + data.get("collectionId").getClass().getName());
//        System.out.println("collectionName: " + data.get("collectionName").getClass().getName());
//        System.out.println("artistName: " + data.get("artistName").getClass().getName());
//        System.out.println("artistId: " + data.get("artistId").getClass().getName());
//        System.out.println("contentAdvisoryRating: " + data.get("contentAdvisoryRating").getClass().getName());
//        System.out.println("primaryGenreName: " + data.get("primaryGenreName").getClass().getName());
//        System.out.println("releaseDate: " + data.get("releaseDate").getClass().getName());
//        System.out.println("artworkUrl600: " + data.get("artworkUrl600").getClass().getName());
//        System.out.println("trackCount: " + data.get("trackCount").getClass().getName());



        parseddata.put("id", data.has("collectionId") ? data.get("collectionId").getValueAsText() : "NA");
        parseddata.put("title",data.has("collectionName") ? data.get("collectionName").getTextValue() : "NA");
        parseddata.put("host",data.has("artistName") ? data.get("artistName").getTextValue() : "NA");
        parseddata.put("hostId",data.has("artistId") ? data.get("artistId").getValueAsText() : "NA");
        parseddata.put("explicit",data.has("contentAdvisoryRating") ? data.get("contentAdvisoryRating").getTextValue() : "NA");
        parseddata.put("description", "NA");
        parseddata.put("genre",data.has("primaryGenreName") ? data.get("primaryGenreName").getTextValue() : "NA");
        parseddata.put("releaseDate",data.has("releaseDate") ? data.get("releaseDate").getTextValue() : "NA");
        parseddata.put("language","NA");
        parseddata.put("coverUrl",data.has("artworkUrl600") ? data.get("artworkUrl600").getTextValue() : "NA");
        parseddata.put("totalTracks",data.has("trackCount") ? data.get("trackCount").getValueAsText() : "NA");

        return parseddata;
    }


    private InputStream getStream(String ENGLISH) throws IOException {
        URL url = new URL(ENGLISH);
        URLConnection connection = url.openConnection();
        connection.setRequestProperty("Accept-Charset", "utf-8");
        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/29.0.1547.62 Safari/537.36");
        connection.setConnectTimeout(CONNECT_TIMEOUT);
        connection.setReadTimeout(READ_TIMEOUT);
        return connection.getInputStream();
    }

    public ArrayList<ObjectNode> pullData(String type, String searchField, String query) throws Exception{

        HashMap<String, String> sd = validateSearchData(type, searchField, query);
        return search(sd.get("type"), sd.get("searchField"), sd.get("query"));
    }


    protected void shutDown() throws Exception {
        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);
        if (log.isInfoEnabled()) {
            log.info("Book data service stopped");
        }
    }


}
