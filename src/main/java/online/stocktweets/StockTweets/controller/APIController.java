package online.stocktweets.StockTweets.controller;

import online.stocktweets.StockTweets.model.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import online.stocktweets.StockTweets.util.Utils;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping(path = "api/v1")
public class APIController {
    StockService ss = new StockService();

    @GetMapping("/")
    public String apiIndex() {
        return getDocumentation(); //TODO Landing page for API
    }

    @GetMapping("/documentation")
    public String getDocumentation() {
        return "documentation.html";
    }


    @GetMapping("/symbols/{industry}")
    @ResponseBody
    public List<?> getSymbols(@PathVariable String industry, HttpServletResponse response) {
        ArrayList<Symbol> symbols = ss.getSymbolList(industry);

        response.setHeader("Access-Control-Allow-Origin", "*");
        return symbols;
    }


    @GetMapping("/tweets/{stockSymbol}")
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
        ArrayList<String> stockList = Utils.readTxtFile("src/main/resources/presetSectors/techStockList.txt");
        for (String s : stockList){
            Stock stock = ss.getStock(s);
            Tweets tweets = ts.getTweets(s);
            StockTweets st = new StockTweets(stock, tweets);
            stockTweets.add(st);
        }


        return stockTweets;
    }
}

