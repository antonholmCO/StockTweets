package online.stocktweets.StockTweets.model;

import online.stocktweets.StockTweets.util.Utils;
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

    /**
     * Top-level method to call external APIs to get data and combine into StockTweets object, creates StockService and TweetService instances
     * @param symbol Company stock symbol
     * @param acceptHeader Value for accept header
     * @return StockTweets-object
     */
   public StockTweets buildStockTweets(String symbol, String acceptHeader) {
        try {

            if(!(acceptHeader.contains("application/json") || acceptHeader.contains("text"))) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only json return type supported");
            }

            if(symbol.length() > 30) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad request, your search string was too long");

            Stock stock = ss.getStock(symbol);

            Utils.startMeasureTime();
            Tweets tweets = ts.getTweets(symbol);
            Utils.measureTime("Get tweets");

            StockTweets st = new StockTweets(stock, tweets);

            return st;

        } catch (HttpClientErrorException e) {
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Too many backend API-requests, try again soon");
        } catch (IndexOutOfBoundsException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Stock not found");
        } catch (NullPointerException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error");
        }
   }
}
