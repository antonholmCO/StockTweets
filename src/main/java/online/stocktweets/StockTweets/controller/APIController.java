package online.stocktweets.StockTweets.controller;

import online.stocktweets.StockTweets.model.Stock;
import online.stocktweets.StockTweets.model.StockTweets;
import online.stocktweets.StockTweets.model.Tweets;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import online.stocktweets.StockTweets.util.Utils;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping(path = "api/v1")
public class APIController {

    private ArrayList presetStockList = new ArrayList<>();



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
        Tweets t2 = null;
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

    @GetMapping("/stocktweets")
    @ResponseBody
    public List getData() {
        ArrayList<StockTweets> stockTweets = new ArrayList<>();

        StockService ss = new StockService();
        TweetService ts = new TweetService();
        ArrayList<String> stocklist = Utils.readFiles.readTxtFile("src/main/resources/topstocks.txt");
        for (String s : stocklist){
            Stock stock = ss.getStock(s);
            Tweets tweets = ts.getTweets(s);
            StockTweets st = new StockTweets(stock, tweets);
            stockTweets.add(st);
        }


        return stockTweets;
    }
}

