package online.stocktweets.StockTweets.controller;

import com.google.gson.Gson;
import online.stocktweets.StockTweets.PasswordsAndKeys;
import online.stocktweets.StockTweets.model.Stock;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;

public class StockService {

    private RestTemplate restTemplate = new RestTemplate();
    private Gson gson = new Gson();

    //Input can be company name or stock symbol
    public Stock getStock(String stock) {
        URI uri = creatStockQuery(stock);

        RestTemplate restTemplate = new RestTemplate();
        String json = restTemplate.getForObject(uri, String.class);

        Gson gson = new Gson();
        Stock stockObj = gson.fromJson(json, Stock.class);


        return stockObj;
    }


    //TODO javadoc
    private URI creatStockQuery(String stockName) {
        StringBuilder strb = new StringBuilder();
        URI uri = null;

        String strURI = strb.append("https://finnhub.io/api/v1/").append(stockName).append(PasswordsAndKeys.finnhubKey).toString();

        try {
            uri = new URI(strURI);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        return uri;
    }

    //TODO javadoc
    public URI createStockQuery(String stockSymbol) {
        StringBuilder strb = new StringBuilder();
        URI uri = null;

        String strURI = strb.append("https://finnhub.io/api/v1/search?q=").append(stockSymbol).append(PasswordsAndKeys.finnhubKey).toString();

        try {
            uri = new URI(strURI);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        return uri;
    }


    /*
    public Stock getStocks(Stock stock) {
        String json = "fel bror";
        try {
            String uri = "https://finnhub.io/api/v1/quote?symbol=AAPL&token=c70ab7qad3id7ammkm3g";
            json = restTemplate.getForObject(uri, String.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Stock getStock = gson.fromJson(json, Stock.class);

        stock.c = getStock.c;
        return stock;

    }
    */
}
