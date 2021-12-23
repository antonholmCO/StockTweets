package online.stocktweets.StockTweets.model;

public class Stock {
    private String description;
    private String symbol;
    private double c;


    public double getPrice() {
        return c;
    }

    public String getName() {
        return description;
    }

    public String getSymbol() {
        return symbol;
    }
}
