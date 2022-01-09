package online.stocktweets.StockTweets.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Stock {
    private String description;
    private String symbol;
    private double c;
    private double o;
    private double percentChange;

    /**
     * Calculates price in SEK based on exchange rate fetched from external API
     * @param priceUSD price in USD
     * @return price in SEK
     */
    private double calculateSEK(double priceUSD) {
        double exchangeRate = ExchangeRate.getExchangeRateUSDToSEK();
        double sek = priceUSD * exchangeRate;

        BigDecimal bigDecimal = BigDecimal.valueOf(sek);
        bigDecimal = bigDecimal.setScale(2, RoundingMode.HALF_UP);

        sek = bigDecimal.doubleValue();

        return sek;
    }

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
    public double getOpenPriceSEK() {
        return calculateSEK(o);
    }
    public double getOpenPriceUSD() {
        return o;
    }
    public double getPercentChange() {
        return percentChange;
    }

    public void setOpenPrice(double o) {
        this.o = o;
    }
    public void setPercentChange(double percentChange) {
        this.percentChange = percentChange;
    }
    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }
    public void setPriceUSD(double c) {
        this.c = c;
    }
    public void setDescription(String description) {
        this.description = description;
    }
}
