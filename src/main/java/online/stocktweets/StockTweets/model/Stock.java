package online.stocktweets.StockTweets.model;

public class Stock {
    private String description;
    private String symbol;
    private double c;



    public double getPriceUSD() {
        return c;
    }

    public String getCompanyName() {
        return description;
    }

    public String getSymbol() {
        return symbol;
    }
    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }
    public double getPriceSEK() {
        return calculateSEK(c);
    }

    private double calculateSEK(double priceUSD) {
        double exchangeRate = ExchangeRate.getExchangeRateUSDToSEK();
        double sek = priceUSD * exchangeRate;

        return sek;
    }

    public static void main(String[] args) {
        Stock stock = new Stock();
        System.out.println(stock.calculateSEK(100));
        Stock stock2 = new Stock();
        System.out.println(stock2.calculateSEK(20));
    }
}
