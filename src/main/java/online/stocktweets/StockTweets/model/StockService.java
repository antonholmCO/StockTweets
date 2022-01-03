package online.stocktweets.StockTweets.model;

import com.google.gson.*;
import online.stocktweets.StockTweets.util.PasswordsAndKeys;
import online.stocktweets.StockTweets.util.Utils;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

public class StockService {

    private RestTemplate restTemplate = new RestTemplate();
    private Gson gson = new Gson();

    //Input can be company name or stock symbol
    public Stock getStock(String stock) throws HttpClientErrorException, NullPointerException {
        URI uri = createSymbolLookupQuery(stock);

        JsonObject jsonObject = createJsonObjectFromURI(uri);
        JsonArray jsonArray = jsonObject.getAsJsonArray("result");

        Gson gson = new Gson();
        Stock stockObj = gson.fromJson(jsonArray.get(0), Stock.class);
        updateStockWithPrice(stockObj);

        return stockObj;
    }

    public ArrayList<Symbol> getSymbolList(String sector) throws HttpClientErrorException, NullPointerException {
        ArrayList<Symbol> symbols = new ArrayList<>();

        if (Sector.SectorVerification.isValidSector(sector.toUpperCase())) {
            ArrayList<String> symbolsStr = Utils.readTxtFile("src/main/resources/presetSectors/"+ sector +"StockList.txt");
            ArrayList<String> marketCap = Utils.readTxtFile("src/main/resources/presetSectors/"+ sector +"MarketCap.txt");

            for (int i = 0; i < symbolsStr.size(); i++) {
                symbols.add(new Symbol(symbolsStr.get(i), Long.parseLong(marketCap.get(i))));
            }

        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Sector doesn't exist");
        }

        return symbols;
    }

    public long getMarketCap(String symbol) {
        long marketCapLong;

        URI uri = createMarketCapQuery(symbol);
        try {
            JsonObject jsonObject = createJsonObjectFromURI(uri);
            double marketCap = jsonObject.get("marketCapitalization").getAsDouble();
            marketCap *= 1000000;
            marketCapLong = (long) marketCap;
        } catch (NullPointerException e) {
            marketCapLong = 0;
            e.printStackTrace();
        }

        return marketCapLong;
    }

    private void updateStockWithPrice(Stock stock) throws HttpClientErrorException {
        URI uri = createPriceQuery(stock.getSymbol());

        JsonObject jsonObject = createJsonObjectFromURI(uri);

        double price = jsonObject.get("c").getAsDouble();
        double percentChange = jsonObject.get("dp").getAsDouble();
        stock.setPriceUSD(price);
        stock.setPercentChange(percentChange);
    }

    private JsonObject createJsonObjectFromURI(URI uri) throws HttpClientErrorException {
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

        return uri;
    }

}
