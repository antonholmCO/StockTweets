package online.stocktweets.StockTweets.controller;

import com.google.gson.*;
import online.stocktweets.StockTweets.util.PasswordsAndKeys;
import online.stocktweets.StockTweets.model.Tweets;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;

public class TweetService {

    private HttpStatus responseCode;


    public Tweets getTweets(String stockSymbol) {
        URI uri = createTweetQuery(stockSymbol);
        String bearerToken = "Bearer " + PasswordsAndKeys.bearerToken;

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", bearerToken);
        headers.set("content-type","application/json; charset=utf-8");
        HttpEntity<String> request = new HttpEntity(headers);

        ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.GET, request, String.class);
        responseCode = response.getStatusCode();
        //TODO hanndle response codes
        if (responseCode == HttpStatus.OK) {
            String strJSON = response.getBody();
            return parseJSON(strJSON);
        } else {
            return null;
        }

    }


    private Tweets parseJSON(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, Tweets.class);
    }


    private URI createTweetQuery(String stockSymbol) {
        StringBuilder strb = new StringBuilder();
        URI uri = null;

        String strURI = strb.append("https://api.twitter.com/2/tweets/search/recent?query=%23").append(stockSymbol).append("%20lang:en").toString();

        try {
            uri = new URI(strURI);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        return uri;
    }


    //Ligger kvar tillsvidare ta inte bort utkommenterat
//    public ArrayList<Tweet> parseTweets(String json) {
//        ArrayList<Tweet> tweets = new ArrayList<>();
//
//        JsonElement jsonElement = JsonParser.parseString(json);
//        JsonObject jsonObject = jsonElement.getAsJsonObject();
//
//        JsonArray jsonArray = jsonObject.getAsJsonArray("data");
//
//        for (JsonElement tweetElement : jsonArray) {
//            JsonObject jsonObj = tweetElement.getAsJsonObject();
//
//            String id = jsonObj.get("id").getAsString();
//            String text = jsonObj.get("text").getAsString();
//
//            tweets.add(new Tweet(id, text));
//        }
//
//        return tweets;
//    }

}
