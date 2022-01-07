package online.stocktweets.StockTweets.util;

import online.stocktweets.StockTweets.model.StockService;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class MarketCapUpdater {
    private int hourInMillis = 3600000;
    private int dayInMillis = 86400000;

    private Timer timer = new Timer();

    public void startTimer() {
        long delay = dayInMillis - (System.currentTimeMillis() % dayInMillis); //TODO Fixa till

        updateCapOnStart();

        timer.schedule(new SectorUpdater("tech"), delay, dayInMillis);
        timer.schedule(new SectorUpdater("medical"), delay+70000, dayInMillis);
        timer.schedule(new SectorUpdater("finance"), delay+140000, dayInMillis);
        timer.schedule(new SectorUpdater("energy"), delay+210000, dayInMillis);
    }


    private void updateCapOnStart() {
        Timer timerFirstUpdate = new Timer();

        timerFirstUpdate.schedule(new SectorUpdater("tech"), 0);
        timerFirstUpdate.schedule(new SectorUpdater("medical"),70000);
        timerFirstUpdate.schedule(new SectorUpdater("finance"),140000);
        timerFirstUpdate.schedule(new SectorUpdater("energy"), 210000);
    }

    private void updateMarketCap(String sector) {
        StockService ss = new StockService();

        ArrayList<String> symbols = Utils.readTxtFile("src/main/resources/presetSectors/"+ sector +"StockList.txt");
        ArrayList<String> marketCap = new ArrayList<>();


        for (String symbol : symbols) {
            long mc = ss.getMarketCap(symbol);
            marketCap.add(Long.toString(mc));
        }

        Utils.writeTxtFile("src/main/resources/presetSectors/" + sector + "MarketCap.txt", marketCap);

        System.out.println(sector + " updated: " + System.currentTimeMillis());
    }

    private class SectorUpdater extends TimerTask {
        private String sector;

        public SectorUpdater(String sector) {
            this.sector = sector;
        }

        @Override
        public void run() {
            updateMarketCap(sector);
        }
    }
}
