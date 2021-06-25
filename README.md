# portfolio-rebalancer

This is portfolio rebalancer app. There are following URLs:

(1) GET request: http://localhost:8081/portfolio-rebalancer/recommendedPortfolioPercentsByRiskPreference/{riskPreference}

(2) POST request: http://localhost:8081/portfolio-rebalancer/rebalancePortfolio/{riskPreference}

body should be of type JSON and should contain an array with exactly 5 double values for example: [120,130,140,360,50]

How to start the app: `mvn spring-boot:run`