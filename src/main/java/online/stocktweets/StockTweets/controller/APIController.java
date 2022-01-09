package online.stocktweets.StockTweets.controller;

import online.stocktweets.StockTweets.model.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping(path = "api/v1")
@CrossOrigin(origins = "*")

public class APIController {

    /**
     * Get mapping for toplevel path
     * @return documentation html page
     */
    @GetMapping("/")
    public String apiIndex() {
        return getDocumentation();
    }

    /**
     * Get mapping for /docs path
     * @return documentation html page
     */
    @GetMapping("/docs")
    public String getDocumentation() {
        return "about_API.html";
    }

    /**
     * Get mapping for endpoint symbols
     * @param sector Stock market sector
     * @return Json file with stock symbols for specified sector
     */
    @GetMapping("/symbols/{sector}")
    @ResponseBody
    public List<?> getSymbols(@PathVariable String sector) {
        return new StockService().getSymbolList(sector);
    }

    /**
     * Get mapping for endpoint stocktweet
     * @param symbol Company stock symbol
     * @return Json file with stock data and corresponding tweets
     */
    @GetMapping("/stocktweet/{symbol}")
    @ResponseBody
    public StockTweets getStockTweets(@PathVariable String symbol) {
        return new StockTweetService().buildStockTweets(symbol);
    }
}

