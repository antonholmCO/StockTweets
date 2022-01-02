package online.stocktweets.StockTweets.controller;

import com.google.gson.*;
import online.stocktweets.StockTweets.model.ExchangeService;
import online.stocktweets.StockTweets.model.Sector;
import online.stocktweets.StockTweets.model.Symbol;
import online.stocktweets.StockTweets.util.PasswordsAndKeys;
import online.stocktweets.StockTweets.model.Stock;
import online.stocktweets.StockTweets.util.Utils;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

public class StockService {

    private RestTemplate restTemplate = new RestTemplate();
    private Gson gson = new Gson();

    //Input can be company name or stock symbol
    public Stock getStock(String stock) {
        URI uri = createSymbolLookupQuery(stock);

        JsonObject jsonObject = getJsonObject(uri);

        JsonArray jsonArray = jsonObject.getAsJsonArray("result");

        Gson gson = new Gson();
        Stock stockObj = gson.fromJson(jsonArray.get(0), Stock.class);


        updateStockWithPrice(stockObj);

        return stockObj;
    }
    public ArrayList<Symbol> getSymbolList(String industry) {
        ArrayList<Symbol> symbols = new ArrayList<>();

        if (Sector.SectorVerification.isValidSector(industry.toUpperCase())) {
            ArrayList<String> symbolsStr = Utils.readTxtFile("src/main/resources/presetSectors/"+ industry +"StockList.txt");
            ArrayList<String> marketCap = Utils.readTxtFile("src/main/resources/presetSectors/"+ industry +"MarketCap.txt");

//            hämta från API istället för från fil

//            for (String symbol : symbolsStr) {
//                long mc = getMarketCap(symbol);
//                marketCap.add(mc);
//            }

            for (int i = 0; i < symbolsStr.size(); i++) {
                symbols.add(new Symbol(symbolsStr.get(i), Long.parseLong(marketCap.get(i))));
            }

        } else {
            System.out.println("COULD NOT FIND, GE ERROR");
            symbols = null;
        }

        return symbols;
    }

    public long getMarketCap(String symbol) {
        URI uri = createMarketCapQuery(symbol);
        JsonObject jsonObject = getJsonObject(uri);

        double marketCap = jsonObject.get("marketCapitalization").getAsDouble();
        marketCap *= 1000000; //TODO checka detta, ger fel, tror det är för tex TSM inte är på US börs och därför ger market cap i TWD istället för USD
        long marketCapLong = (long) marketCap;

        return marketCapLong;
    }

    private void updateStockWithPrice(Stock stock) {
        URI uri = createPriceQuery(stock.getSymbol());

        JsonObject jsonObject = getJsonObject(uri);

        double price = jsonObject.get("c").getAsDouble();
        stock.setPriceUSD(price);
    }

    private JsonObject getJsonObject(URI uri) {
        RestTemplate restTemplate = new RestTemplate();
        String json = restTemplate.getForObject(uri, String.class);

        JsonElement jsonElement = JsonParser.parseString(json);
        JsonObject jsonObject = jsonElement.getAsJsonObject();

        return jsonObject;
    }

    private URI createSymbolLookupQuery(String stockName) {
        StringBuilder strb = new StringBuilder();
        URI uri = null;
        String strURI = strb.append("https://finnhub.io/api/v1/search?q=").append(stockName).append(PasswordsAndKeys.finnhubKey).toString();

        try {
            uri = new URI(strURI);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        return uri;
    }

    private URI createPriceQuery(String stockSymbol) {
        StringBuilder strb = new StringBuilder();
        URI uri = null;
        String strURI = strb.append("https://finnhub.io/api/v1/quote?symbol=").append(stockSymbol).append(PasswordsAndKeys.finnhubKey).toString();

        try {
            uri = new URI(strURI);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        return uri;
    }

    private URI createMarketCapQuery(String stockSymbol) {
        StringBuilder strb = new StringBuilder();
        URI uri = null;
        String strURI = strb.append("https://finnhub.io/api/v1/stock/profile2?symbol=").append(stockSymbol).append(PasswordsAndKeys.finnhubKey).toString();

        try {
            uri = new URI(strURI);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        System.out.println(uri);

        return uri;
    }

}
