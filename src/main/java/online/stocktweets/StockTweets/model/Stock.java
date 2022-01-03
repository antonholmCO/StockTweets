package online.stocktweets.StockTweets.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Stock {
    private String description;
    private String symbol;
    private double c;


    public String getCompanyName() {
        return description;
    }

    public String getSymbol() {
        return symbol;
    }

    public double getPriceUSD() {
        return c;
    }
    public double getPriceSEK() {
        return calculateSEK(c);
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public void setPriceUSD(double c) {
        this.c = c;
    }

    private double calculateSEK(double priceUSD) {
        double exchangeRate = ExchangeRate.getExchangeRateUSDToSEK();
        double sek = priceUSD * exchangeRate;

        BigDecimal bigDecimal = BigDecimal.valueOf(sek);
        bigDecimal = bigDecimal.setScale(2, RoundingMode.HALF_UP);

        sek = bigDecimal.doubleValue();

        return sek;

    }
}
