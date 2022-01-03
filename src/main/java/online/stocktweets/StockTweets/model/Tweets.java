package online.stocktweets.StockTweets.model;

import java.util.List;

public class Tweets {
    public List<Data> data;

    Data l = new Data();

    public class Data {
        private String author_id;
        public String id;
        public String text;
        public String authorName;

        public Data() {

        }

        public String authorIdkompis() {
            return getAuthor_id();
        }

        private String getAuthor_id() {
            return author_id;
        }
        public String getId() {
            return id;
        }
        public String getText() {
            return text;
        }
        public void setAuthor(String authorName) {
            this.authorName = authorName;
        }

        public String getAuthorName() {
            return authorName;
        }

        @Override
        public String toString() {
            return author_id + " " + id + " " + text;
        }
    }
    public List<Data> getData(){
        return data;
    }
}
