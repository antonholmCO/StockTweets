package online.stocktweets.StockTweets.util;

import online.stocktweets.StockTweets.controller.StockService;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class MarketCapUpdater {
    private int hourInMillis = 3600000;
    Timer timer = new Timer();

    public void startTimer() {
        long delay = hourInMillis * 24L - (System.currentTimeMillis() % (hourInMillis * 24L));

        timer.schedule(new UpdateTech(), 5000, delay);
        timer.schedule(new UpdateMedical(), 75000, delay);
    }

    private class UpdateTech extends TimerTask {
        @Override
        public void run() {
            StockService ss = new StockService();

            ArrayList<String> symbols = Utils.readTxtFile("src/main/resources/presetSectors/techStockList.txt");

            ArrayList<String> marketCap = new ArrayList<>();


            for (String symbol : symbols) {
                long mc = ss.getMarketCap(symbol);
                marketCap.add(Long.toString(mc));
            }

            Utils.writeTxtFile("src/main/resources/presetSectors/techMarketCap.txt", marketCap);

            System.out.println("Tech updated: " + System.currentTimeMillis());
        }
    }

    private class UpdateMedical extends TimerTask {
        @Override
        public void run() {
            StockService ss = new StockService();

            ArrayList<String> symbols = Utils.readTxtFile("src/main/resources/presetSectors/medicalStockList.txt");

            ArrayList<String> marketCap = new ArrayList<>();

            for (String symbol : symbols) {
                long mc = ss.getMarketCap(symbol);
                marketCap.add(Long.toString(mc));
            }

            Utils.writeTxtFile("src/main/resources/presetSectors/medicalMarketCap.txt", marketCap);

            System.out.println("Medical updated: " + System.currentTimeMillis());
        }
    }
}
