package online.stocktweets.StockTweets.model;

public enum Sector {
    TECH,
    MEDICAL,
    FINANCE,
    ENERGY;

    public static class SectorVerification {
        /**
         * Checks if enum contains stock market sector
         * @param sector
         * @return boolean if sector is valid
         */
        public static boolean isValidSector(String sector) {

            for (Sector i : Sector.values()) {
                if (i.name().equals(sector)) {
                    return true;
                }
            }

            return false;
        }
    }

}
