package online.stocktweets.StockTweets.model;

public class ExchangeRate {

    private static ExchangeService es = new ExchangeService();

    private static int oneHourMillis = 3600000;
    private static int hoursBetweenCalls = 12;

    private static long lastExchangeRateCallMillis = 0;
    private static double rate = 0.0;

    /**
     * Requests external API for current exchange rate from USD to SEK if rate has not been requests in the recent 12 hours
     * @return exchange rate
     */
    public static double getExchangeRateUSDToSEK() {
        long timeSinceLastExchangeRateCall = System.currentTimeMillis() - lastExchangeRateCallMillis;

            if (timeSinceLastExchangeRateCall > ((long) oneHourMillis * hoursBetweenCalls) ) {
                rate = es.getExchangeRate("USD", "SEK");
                lastExchangeRateCallMillis = System.currentTimeMillis();
            }

            return rate;
    }
}
