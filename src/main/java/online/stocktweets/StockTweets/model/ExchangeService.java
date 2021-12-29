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

    public double getExchangeRate(String curr1, String curr2) {

        StringBuilder strb = new StringBuilder();
        strb.append("https://v6.exchangerate-api.com/v6/")
                .append(PasswordsAndKeys.exchangeAPIKey)
                .append("/pair/")
                .append(curr1)
                .append("/")
                .append(curr2);

        String json = null;

        try {
            URI uri = new URI(strb.toString());
            json = restTemplate.getForObject(uri, String.class);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        JsonElement jsonElement = JsonParser.parseString(json);
        JsonObject jsonObject = jsonElement.getAsJsonObject();

        double rate = jsonObject.get("conversion_rate").getAsDouble();

        return rate;
    }

}
