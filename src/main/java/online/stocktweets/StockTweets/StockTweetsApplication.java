package online.stocktweets.StockTweets;

import online.stocktweets.StockTweets.util.MarketCapUpdater;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
public class StockTweetsApplication {

	public static void main(String[] args) {
		SpringApplication.run(StockTweetsApplication.class, args);
		new MarketCapUpdater().startTimer();
	}

}
