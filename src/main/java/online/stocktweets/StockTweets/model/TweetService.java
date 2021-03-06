package online.stocktweets.StockTweets.model;

import com.google.gson.*;
import online.stocktweets.StockTweets.util.PasswordsAndKeys;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

public class TweetService {

    private HttpStatus responseCode;
    private final String bearerToken = "Bearer " + PasswordsAndKeys.bearerToken;

    /**
     * Collection method to get Tweets from external API
     * @param stockSymbol Stock symbol of company
     * @return Tweets-object
     */
    public Tweets getTweets(String stockSymbol) {
        Tweets tweets = getOnlyTweets(stockSymbol);
        populateUsernames(tweets);

        return tweets;
    }

    /**
     * Gets tweets from external API
     * @param stockSymbol Stock symbol of company
     * @return Tweets-object
     */
    private Tweets getOnlyTweets(String stockSymbol) {
        URI uri = createTweetQuery(stockSymbol);

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", bearerToken);
        headers.set("content-type", "application/json; charset=utf-8");
        HttpEntity<String> request = new HttpEntity(headers);

        ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.GET, request, String.class);
        responseCode = response.getStatusCode();
        if (responseCode == HttpStatus.OK) {
            String strJSON = response.getBody();
            return parseJSON(strJSON);
        } else {
            return null;
        }
    }

    /**
     * Requests usernames from Twitter API and sets them for all tweets in tweets objects based on their Twitter author id
     * @param tweets With author ids
     */
    private void populateUsernames(Tweets tweets) {
        ArrayList<String> authorIds = new ArrayList<>();

        for (Tweets.Data tweet : tweets.getData()) {
            authorIds.add(tweet.authorIdGetter());
        }
        URI uri = createTweetUserQuery(authorIds);

        JsonObject json = getJsonObject(uri);
        JsonArray jsonArray = json.getAsJsonArray("data");

        int i = 0;
        for (JsonElement user : jsonArray) {
            String out = user.getAsJsonObject().get("username").toString();
            out = out.substring(1, out.length() - 1);

            tweets.getData().get(i++).setAuthor(out);
        }
    }

    /**
     * This method creates a JsonObject from a URI, used to reduce duplicate code
     * @param uri URI-object
     * @return JsonObject
     */
    private JsonObject getJsonObject(URI uri) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();

        headers.set("Authorization", bearerToken);
        headers.set("content-type", "application/json; charset=utf-8");

        HttpEntity<String> request = new HttpEntity(headers);
        ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.GET, request, String.class);

        JsonElement jsonElement = JsonParser.parseString(response.getBody());
        JsonObject jsonObject = jsonElement.getAsJsonObject();

        return jsonObject;
    }

    /**
     * This method parses Json strings to objects
     * @param json Json string
     * @return Tweets-object
     */
    private Tweets parseJSON(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, Tweets.class);
    }

    /**
     * Creates a URI for external API-call
     * @param stockSymbol
     * @return URI for API request
     */
    private URI createTweetQuery(String stockSymbol) {
        StringBuilder strb = new StringBuilder();
        URI uri = null;

        String strURI = strb.append("https://api.twitter.com/2/tweets/search/recent?query=%23")
                .append(stockSymbol)
                .append("%20lang:en")
                .append("&expansions=author_id")
                .toString();

        try {
            uri = new URI(strURI);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        return uri;
    }

    /**
     * Creates a URI for external API-call
     * @param authorIdList
     * @return URI for API request
     */
    private URI createTweetUserQuery(ArrayList<String> authorIdList) {
        StringBuilder strb = new StringBuilder();
        URI uri = null;

        strb.append("https://api.twitter.com/2/users?ids=");

        for (String authorId : authorIdList) {
            strb.append(authorId).append(",");
        }

        strb.delete(strb.length() - 1, strb.length());
        String strURI = strb.toString();

        try {
            uri = new URI(strURI);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        return uri;
    }
}

