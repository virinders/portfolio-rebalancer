
# portfolio-rebalancer

This is portfolio rebalancer app. It was written as a REST API app with Spring Boot 2.5.1 and JDK 1.8.

There are following URLs:

(1) **GET** request: `http://localhost:8081/portfolio-rebalancer/portfolio/{riskPreference}`

Returns the recommended portfolio allocation between BONDS, LARGE_CAP, MID_CAP, FOREIGN_CAP, SMALL_CAP based on input riskPreference.

(2) **POST** request: `http://localhost:8081/portfolio-rebalancer/portfolio/rebalance/{riskPreference}`

Returns the recommended transference between investment categories based on user's current allocation amounts and desired allocation.

{riskPreference}  input parameter should be specified as a number between 1-10.

Body should be of type JSON and should contain an array with exactly 5 elements as follows: (the individual pair of entries can be in any order)

    [{"category":"BONDS","amount":"80.0"},{"category":"LARGE_CAP","amount":"57.0"},{"category":"MID_CAP","amount":"40.0"},{"category":"FOREIGN_CAP","amount":"50.0"},{"category":"SMALL_CAP","amount":"98.0"}]

These APIs can be tested with Postman.

How to start the app: `mvn spring-boot:run`
