package gov.ca.portfolio.rebalancer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PortfolioRebalanceApplication {

	/**
	 * Main method to start this service.
	 *
	 * @param args
	 *            input params.
	 */
	public static void main(String[] args) {
		SpringApplication.run(PortfolioRebalanceApplication.class, args);
	}
}
