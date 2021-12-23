package online.stocktweets.StockTweets.model;

import java.util.List;

public class Tweet {
    public List<Data> data;

    Data l = new Data();

    public class Data {
        public String userID;
        public String id;
        public String text;

        public Data() {

        }

        public Data(String userID, String tweetId, String text) {
            this.userID = userID;
            id = tweetId;
            this.text = text;
        }

        public String getId() {
            return id;
        }
        public String getText() {
            return text;
        }


        @Override
        public String toString() {
            return id + " " + text;
        }
    }
}
