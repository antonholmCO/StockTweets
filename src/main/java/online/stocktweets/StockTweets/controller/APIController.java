package online.stocktweets.StockTweets.controller;

import online.stocktweets.StockTweets.model.Tweet;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(path = "api/v1")
public class APIController {



    @GetMapping("/tweets")
    public List getTweets() {
        TweetService ts = new TweetService();
//        ArrayList<Tweet> tweets = ts.getTweetsFromTwitter("1228393702244134912,1227640996038684673,1199786642791452673");
        Tweet t2 = null;
        try {
            t2 = ts.getTweets("");
        } catch (Exception e) {
        }

        if(t2 == null) {
            ArrayList<String> ar = new ArrayList();
            ar.add(HttpStatus.BAD_REQUEST.toString());

            return ar;
        }

        return t2.data;
    }




    @GetMapping("/tweet/{id}")
    public int getDrink1(@PathVariable int id) {
        return id;
    }
}

