package online.stocktweets.StockTweets.model;

import java.util.ArrayList;

public class StockTweets {

    private Stock stock;
    private Tweets tweets;

    public StockTweets(Stock stock, Tweets tweets) {
        this.stock = stock;
        this.tweets = tweets;
    }

    public Stock getStock() {
        return stock;
    }

    public void setStock(Stock stock) {
        this.stock = stock;
    }

    public Tweets getTweets() {
        return tweets;
    }

    public void setTweets(Tweets tweets) {
        this.tweets = tweets;
    }
}
