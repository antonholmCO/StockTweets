package online.stocktweets.StockTweets.model;

public class ExchangeRate {
    private static ExchangeService es = new ExchangeService();
    private static int oneHourInMS = 3600000;
    private static long timeSinceLastExchangeRate = -1;
    private static long latestExchangeRateCall = 0;
    private static double currentRate = 0.0;
    private static int hours = 12;


    public static double getExchangeRateUSDToSEK() {
        timeSinceLastExchangeRate = System.currentTimeMillis() - latestExchangeRateCall;
        System.out.println("time since last exch check: " + timeSinceLastExchangeRate);

        if (timeSinceLastExchangeRate > ((long) oneHourInMS * hours) ) {
            currentRate = es.getExchangeRate("USD", "SEK");
            latestExchangeRateCall = System.currentTimeMillis();
        }

        System.out.println("currRate: " + currentRate);
        return currentRate;
    }
}
