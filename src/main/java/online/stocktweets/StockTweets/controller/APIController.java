package online.stocktweets.StockTweets.controller;

import online.stocktweets.StockTweets.model.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping(path = "api/v1")
@CrossOrigin(origins = "*")

public class APIController {

    @GetMapping("/")
    public String apiIndex() {
        return getDocumentation();
    }

    @GetMapping("/documentation")
    public String getDocumentation() {
        return "about_API.html";
    }

    @GetMapping("/symbols/{sector}")
    @ResponseBody
    public List<?> getSymbols(@PathVariable String sector) {
        return new StockService().getSymbolList(sector);
    }

    @GetMapping("/stocktweet/{symbol}")
    @ResponseBody
    public StockTweets getStockTweets(@PathVariable String symbol) {
        return new StockTweetService().buildStockTweets(symbol);
    }
}

