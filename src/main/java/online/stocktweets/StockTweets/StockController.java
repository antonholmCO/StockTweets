package online.stocktweets.StockTweets;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class StockController {
    @GetMapping
    public String getDrink(Model model) {
        model.addAttribute("something", "hello kaos");
        return "index";
    }
}
