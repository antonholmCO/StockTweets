package online.stocktweets.StockTweets.controller;

import online.stocktweets.StockTweets.model.Stock;
import online.stocktweets.StockTweets.model.Tweet;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping(path = "api/v1")
public class APIController {

    @GetMapping("/")
    public String apiIndex() {
        return getDocumentation(); //TODO Landing page for API
    }

    @GetMapping("/documentation")
    public String getDocumentation() {
        return "documentation.html";
    }

    @GetMapping("/tweets/{stockSymbol}")
    @ResponseBody
    public List getTweets(@PathVariable String stockSymbol) {
        TweetService ts = new TweetService();
//        ArrayList<Tweet> tweets = ts.getTweetsFromTwitter("1228393702244134912,1227640996038684673,1199786642791452673");
        Tweet t2 = null;
        try {
            t2 = ts.getTweets(stockSymbol);
        } catch (Exception e) {
        }

        if(t2 == null) {
            ArrayList<String> ar = new ArrayList();
            ar.add(HttpStatus.BAD_REQUEST.toString());

            return ar;
        }

        return t2.data;
    }


    @GetMapping("/stock/{name}")
    @ResponseBody
    public List getDrink1(@PathVariable String name) {
        StockService ss = new StockService();
        Stock stock = ss.getStock(name);
        ArrayList <Stock> stocks = new ArrayList<>();
        stocks.add(stock);
        return stocks;
    }
}

