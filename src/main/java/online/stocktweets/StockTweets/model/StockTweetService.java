package online.stocktweets.StockTweets.model;

import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.server.ResponseStatusException;

public class StockTweetService {

    private StockService ss;
    private TweetService ts;

    public StockTweetService() {
        ss = new StockService();
        ts = new TweetService();
   }

   public StockTweets buildStockTweets(String symbol) {
        try {
            Stock stock = ss.getStock(symbol);
            Tweets tweets = ts.getTweets(symbol);
            StockTweets st = new StockTweets(stock, tweets);
            return st;
        } catch (HttpClientErrorException e) {
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Too many backend API-requests, try again soon");
        } catch (NullPointerException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Error, du hittar mig på blip.se ante_2");
        }
   }
}
