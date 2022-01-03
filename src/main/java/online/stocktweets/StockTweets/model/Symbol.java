package online.stocktweets.StockTweets.model;

public class Symbol {
    private String symbol;
    private long marketCap;

    public Symbol(String symbol, long marketCap) {
        this.symbol = symbol;
        this.marketCap = marketCap;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public long getMarketcap() {
        return marketCap;
    }

    public void setMarketCap(int marketCap) {
        this.marketCap = marketCap;
    }
}
