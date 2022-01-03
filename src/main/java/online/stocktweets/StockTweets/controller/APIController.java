package online.stocktweets.StockTweets.controller;

import online.stocktweets.StockTweets.model.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
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

    @GetMapping("/symbols/{sector}")
    @ResponseBody
    public List<?> getSymbols(@PathVariable String sector, HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "*");

        return new StockService().getSymbolList(sector);
    }

    @GetMapping("/stocktweet/{symbol}")
    @ResponseBody
    public StockTweets getData(@PathVariable String symbol, HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "*");

        return new StockTweetService().buildStockTweets(symbol);
    }

    /*    @GetMapping("/tweets/{stockSymbol}")
    @ResponseBody
    public List getTweets(@PathVariable String stockSymbol, HttpServletResponse response) {
        TweetService ts = new TweetService();
        Tweets t2 = null;
        try {
            t2 = ts.getTweets(stockSymbol);
        } catch (Exception e) {
        }

        if(t2 == null) {
            ArrayList<String> ar = new ArrayList<>();
            ar.add(HttpStatus.BAD_REQUEST.toString());

            return ar;
        }
        response.setHeader("Access-Control-Allow-Origin", "*");
        return t2.data;
    }*/


/*    @GetMapping("/stock/{name}")
    @ResponseBody
    public List getDrink1(@PathVariable String name) {
        StockService ss = new StockService();
        Stock stock = ss.getStock(name);
        ArrayList <Stock> stocks = new ArrayList<>();
        stocks.add(stock);
        return stocks;
    }*/
}

