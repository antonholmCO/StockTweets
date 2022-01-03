package online.stocktweets.StockTweets.model;

import java.util.List;

public class Tweets {
    public List<Data> data;

    Data l = new Data();

    public class Data {
        public String id;
        public String text;

        public Data() {

        }

        public Data(String tweetId, String text) {
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
