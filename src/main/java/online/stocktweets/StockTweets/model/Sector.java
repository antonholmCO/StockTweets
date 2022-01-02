package online.stocktweets.StockTweets.model;

public enum Sector {
    TECH,
    MEDICAL,
    FINANCE,
    ENERGY;

    public static class SectorVerification {
        public static boolean isValidSector(String industry) {

            for (Sector i : Sector.values()) {
                if (i.name().equals(industry)) {
                    return true;
                }
            }

            return false;
        }
    }

}
