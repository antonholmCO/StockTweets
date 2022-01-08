package online.stocktweets.StockTweets.model;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import online.stocktweets.StockTweets.util.PasswordsAndKeys;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;

public class ExchangeService {
    private RestTemplate restTemplate = new RestTemplate();

    /**
     * Gets exchange rate on two different currencies, from first parameter input to second parameter input
     * @param curr1 first currency abbreviation, example USD
     * @param curr2 second currency abbreviation, example SEK
     * @return exchange rate for selected currencies
     */
    public double getExchangeRate(String curr1, String curr2) {

        StringBuilder strb = new StringBuilder();

        strb.append("https://v6.exchangerate-api.com/v6/")
                .append(PasswordsAndKeys.exchangeAPIKey)
                .append("/pair/")
                .append(curr1)
                .append("/")
                .append(curr2);

        String json;
        JsonObject jsonObject = null;

        try {
            URI uri = new URI(strb.toString());
            json = restTemplate.getForObject(uri, String.class);
            JsonElement jsonElement = JsonParser.parseString(json);
            jsonObject = jsonElement.getAsJsonObject();

        } catch (URISyntaxException | NullPointerException e) {
            e.printStackTrace();
        }

        return jsonObject.get("conversion_rate").getAsDouble();
    }

}
