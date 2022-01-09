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

    /**
     * Performs a call to Finnhub API to get stock-data for a specified company via its stock symbol
     * @param stock String stock symbol
     * @return Stock-object
     * @throws HttpClientErrorException If an error occurs when calling the external API this method throws the http exception
     * @throws NullPointerException If an error occurs during creation of the Stock-object this method throws this exception
     */
    public Stock getStock(String stock) throws HttpClientErrorException, NullPointerException, IndexOutOfBoundsException {
        URI uri = createSymbolLookupQuery(stock);

        JsonObject jsonObject = createJsonObjectFromURI(uri);
        JsonArray jsonArray = jsonObject.getAsJsonArray("result");

        Gson gson = new Gson();
        Stock stockObj = gson.fromJson(jsonArray.get(0), Stock.class);

        updateStockWithPrice(stockObj);

        return stockObj;
    }

    public Stock getDummyStock() {
        Stock stock = new Stock();
        stock.setSymbol("AAPL");
        stock.setPercentChange(2.5004);
        stock.setDescription("APPLE INC");
        stock.setPriceUSD(182.01);
        stock.setOpenPrice(181.0);
        return stock;
    }

    /**
     * Creates a list of symbol objects with symbol name and corresponding market cap
     * @param sector That is predefined, enum sector should contain passed in sector
     * @return List of symbol objects
     * @throws HttpClientErrorException If an error occurs when calling the external API this method throws the http exception
     * @throws NullPointerException If an error occurs during creation of the Stock-object this method throws this exception
     */
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

    /**
     * Makes an external API call to get market cap of stock symbol
     * @param symbol Stock symbol for company
     * @return Market cap of company in USD
     */
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

    /**
     * Makes a call to the external API to get the current price, open price and percent change for the Stock in question
     * @param stock Stock-object
     * @throws HttpClientErrorException If an error occurs when calling the external API an exception will be thrown
     */
    private void updateStockWithPrice(Stock stock) throws HttpClientErrorException {
        URI uri = createPriceQuery(stock.getSymbol());

        JsonObject jsonObject = createJsonObjectFromURI(uri);

        double price = jsonObject.get("c").getAsDouble();
        double openPrice = jsonObject.get("o").getAsDouble();
        double percentChange = jsonObject.get("dp").getAsDouble();
        stock.setPriceUSD(price);
        stock.setPercentChange(percentChange);
        stock.setOpenPrice(openPrice);
    }

    /**
     * This method creates a JsonObject from a URI, used to reduce duplicate code
     * @param uri URI-object
     * @return JsonObject
     * @throws HttpClientErrorException If an error occurs when calling the external API an exception will be thrown
     */
    private JsonObject createJsonObjectFromURI(URI uri) throws HttpClientErrorException {
        RestTemplate restTemplate = new RestTemplate();

        String json = restTemplate.getForObject(uri, String.class);

        JsonElement jsonElement = JsonParser.parseString(json);
        JsonObject jsonObject = jsonElement.getAsJsonObject();

        return jsonObject;
    }

    /**
     * Creates a URI for external API-call
     * @param stockName
     * @return URI for API request
     */
    private URI createSymbolLookupQuery(String stockName) {
        StringBuilder strb = new StringBuilder();
        URI uri = null;
        String strURI = strb.append("https://finnhub.io/api/v1/search?q=")
                .append(stockName)
                .append(PasswordsAndKeys.finnhubKey)
                .toString();

        try {
            uri = new URI(strURI);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        return uri;
    }

    /**
     * Creates a URI for external API-call
     * @param stockSymbol
     * @return URI for API request
     */
    private URI createPriceQuery(String stockSymbol) {
        StringBuilder strb = new StringBuilder();
        URI uri = null;
        String strURI = strb.append("https://finnhub.io/api/v1/quote?symbol=")
                .append(stockSymbol)
                .append(PasswordsAndKeys.finnhubKey)
                .toString();

        try {
            uri = new URI(strURI);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        return uri;
    }

    /**
     * Creates a URI for external API-call
     * @param stockSymbol
     * @return URI for API request
     */
    private URI createMarketCapQuery(String stockSymbol) {
        StringBuilder strb = new StringBuilder();
        URI uri = null;
        String strURI = strb.append("https://finnhub.io/api/v1/stock/profile2?symbol=")
                .append(stockSymbol)
                .append(PasswordsAndKeys.finnhubKey)
                .toString();

        try {
            uri = new URI(strURI);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        return uri;
    }

}
