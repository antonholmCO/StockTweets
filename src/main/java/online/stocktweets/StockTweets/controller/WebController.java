package online.stocktweets.StockTweets.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class WebController {
    @GetMapping("/")
    public String getIndex() {
        return "index.html";
    }
}
