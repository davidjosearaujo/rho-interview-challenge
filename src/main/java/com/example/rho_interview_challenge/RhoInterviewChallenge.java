package com.example.rho_interview_challenge;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@SpringBootApplication
public class RhoInterviewChallenge {

	@Value("${exchange.rate.api.url}")
	private String exchangeRateApiUrl;

	public static void main(String[] args) {
		SpringApplication.run(RhoInterviewChallenge.class, args);
	}

	// Get exchange rates for a currency A to all or to a specific currency B
	// If currency parameter is not provided, return all exchange rates for currency
	// A in the path variable
	// If multiple currencies parameters are provided, return a list of exchange
	// rates to each currency
	// If the currency is not found, return an error

	// Example: GET /rate/USD?currency=EUR
	// Example: GET /rate/USD?currency=EUR&currency=JPY
	// Example: GET /rate/USD

	@GetMapping(value = "/rate/{id}")
	public ResponseEntity<Object> getRate(@PathVariable("id") String id,
			@RequestParam(required = false) List<String> currency) {
		return new ResponseEntity<>("Hi", HttpStatus.OK);
	}

	// Get value conversion for a currency A to all or to a the value in currency B
	// If currency parameter is not provided, return all values in all currencies
	// If multiple currencies parameters are provided, return a list of values in
	// each currency
	// If the currency is not found, return an error

	// Example: GET /value/USD/100?currency=EUR
	// Example: GET /value/USD/100?currency=EUR&currency=JPY
	// Example: GET /value/USD/100

	@GetMapping(value = "/value/{id}/{amount}")
	public ResponseEntity<Object> getValue(@PathVariable("id") String id, @PathVariable("amount") String amount,
			@RequestParam(required = false) List<String> currency) {
		return new ResponseEntity<>("Hi", HttpStatus.OK);
	}

	private static void providerRates() {

	}
}