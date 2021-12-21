package online.stocktweets.StockTweets;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "api/v1")
public class APIController {
    @GetMapping("/tweets")
    public String getDrink() {
        return "hej";
    }

    @GetMapping("/tweet/{id}")
    public int getDrink1(@PathVariable int id) {
        return id;
    }
}
