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

public class SpotifyImport implements AudioImportStrategy{

    private static final Logger log = LoggerFactory.getLogger(SpotifyImport.class);

    private static String authToken = "BQAbXMO9ODKW5zH5Oq45jw7Jd4cm_Jo1Su9T1tdsUuV2kVHkJNvAX3j3NX0MNsqlH-zt0TP0t2RN2J-TI8bn4epLjA8cUsICQ-gTzk12TDCjM1EhC9E";

    /**
     * Spotify API Search URL.
     */
    private static final String spotify_api_search_endpoint = "https://api.spotify.com/v1/search?q=%s&type=%s&limit=30&market=US";

    private static final int CONNECT_TIMEOUT = 10000;
    private static final int READ_TIMEOUT = 10000;

    /**
     * Executor for spotify API requests.
     */
    private ExecutorService executor;


    /**
     * Spotify API rate limiter.
     */
    private RateLimiter spotifyRateLimiter = RateLimiter.create(20);


    protected void startUp() throws Exception {
        executor = Executors.newSingleThreadExecutor();

        if (log.isInfoEnabled()) {
            log.info("Audio import service started");
        }
    }

    private HashMap<String, String> validateSearchData(String type, String query) throws Exception {
        if (!type.equalsIgnoreCase("audiobook") && !type.equalsIgnoreCase("podcast")) {
            throw new Exception("audio type not valid");
        }

        HashMap<String, String> sd = new HashMap<>();

        if (type.equalsIgnoreCase("podcast")) {
            sd.put("type", "episode"); // works!!!
        } else {
            sd.put("type", type);
        }
        sd.put("query", query);

        return sd;
    }

    private ArrayList<ObjectNode> search(String type, String query) throws Exception {
        spotifyRateLimiter.acquire();

        InputStream inputStream = getStream(String.format(Locale.ENGLISH, spotify_api_search_endpoint, query, type));
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readValue(inputStream, JsonNode.class);

        if (rootNode.get(type + "s").get("total").getIntValue() <= 0) {
            throw new Exception("No audio found");
        }

        ArrayNode datalist = (ArrayNode) rootNode.get(type + "s").get("items");
        ArrayList<ObjectNode> audio = new ArrayList<ObjectNode>();

        for (JsonNode jsonnode : datalist) {
            if (type.equalsIgnoreCase("audiobook")) {
                audio.add(createAudioBookObject((ObjectNode) jsonnode));
            } else if (type.equalsIgnoreCase("episode")) {
                audio.add(createPodcastObject((ObjectNode) jsonnode));
            }
        }
        return audio;
    }

    private ObjectNode createAudioBookObject(ObjectNode data) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode parseddata = mapper.createObjectNode();

        parseddata.put("id", data.has("id") ? data.get("id").getValueAsText() : "NA");
        parseddata.put("title", data.has("name") ? data.get("name").getTextValue() : "NA");

        ArrayNode authorList = (ArrayNode) data.get("authors");
        String authors = "";

        for (JsonNode jsonnode : authorList) {
            authors = authors + ", " + jsonnode.get("name").getTextValue();
        }
        authors = authors.substring(1);
        parseddata.put("author", authors);

        parseddata.put("authorId", "NA");

        ArrayNode narratorList = (ArrayNode) data.get("narrators");
        String narrators = "";

        for (JsonNode jsonnode : narratorList) {
            narrators = narrators + ", " + jsonnode.get("name").getTextValue();
        }
        narrators = narrators.substring(1);
        parseddata.put("narrator", narrators);

        parseddata.put("explicit", data.has("explicit") ? data.get("explicit").getTextValue() : "NA");
        parseddata.put("description", data.has("description") ? data.get("description").getTextValue() : "NA");
        parseddata.put("genre", "NA");
        parseddata.put("releaseDate", "NA");

        ArrayNode languageList = (ArrayNode) data.get("languages");
        String languages = "";

        for (JsonNode lang : languageList) {
            languages = languages + ", " + lang.getTextValue();
        }
        languages = languages.substring(1);
        parseddata.put("language", languages);

        ArrayNode urlList = (ArrayNode) data.get("images");
        String url = urlList.get(0).get("url").getTextValue();
        parseddata.put("coverUrl", url);

        return parseddata;
    }

    private ObjectNode createPodcastObject(ObjectNode data) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode parseddata = mapper.createObjectNode();

        parseddata.put("id", data.has("id") ? data.get("id").getValueAsText() : "NA");
        parseddata.put("title", data.has("name") ? data.get("name").getTextValue() : "NA");
        parseddata.put("host", data.has("publisher") ? data.get("publisher").getTextValue() : "NA");
        parseddata.put("hostId", "NA");
        parseddata.put("explicit", data.has("explicit") ? data.get("explicit").getTextValue() : "NA");
        parseddata.put("description", data.has("description") ? data.get("description").getTextValue() : "NA");
        parseddata.put("genre", "NA");
        parseddata.put("releaseDate", "NA");

        ArrayNode languageList = (ArrayNode) data.get("languages");
        String languages = "";

        for (JsonNode lang : languageList) {
            languages = languages + ", " + lang.getTextValue();
        }
        languages = languages.substring(1);
        parseddata.put("language", languages);

        ArrayNode urlList = (ArrayNode) data.get("images");
        String url = urlList.get(0).get("url").getTextValue();
        parseddata.put("coverUrl", url);

        parseddata.put("totalTracks", data.has("total_episodes") ? data.get("total_episodes").getValueAsText() : "NA");

        return parseddata;
    }


    private InputStream getStream(String ENGLISH) throws IOException {
        URL url = new URL(ENGLISH);
        URLConnection connection = url.openConnection();
        connection.setRequestProperty("Accept-Charset", "utf-8");
        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/29.0.1547.62 Safari/537.36");
        connection.setRequestProperty("Authorization", "Bearer " + authToken);
        connection.setConnectTimeout(CONNECT_TIMEOUT);
        connection.setReadTimeout(READ_TIMEOUT);
        return connection.getInputStream();
    }

    public ArrayList<ObjectNode> pullData(String type, String searchField, String query) throws Exception {
        HashMap<String, String> sd = validateSearchData(type, query);
        return search(sd.get("type"), sd.get("query"));
    }


    protected void shutDown() throws Exception {
        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);
        if (log.isInfoEnabled()) {
            log.info("Book data service stopped");
        }

    }
}