package online.stocktweets.StockTweets.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class WebController {
    /**
     * Get mapping for landing page
     * @return index file
     */
    @GetMapping("/")
    public String getIndex() {
        return "index.html";
    }
}
