package com.example.rho_interview_challenge.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.rho_interview_challenge.ExchangeRate;
import com.example.rho_interview_challenge.RhoInterviewChallenge;
import com.example.rho_interview_challenge.service.ExchangeRateService;

import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/exchange")
@Tag(name = "Exchange Rate Service", description = "Provides currency exchange rate information")
public class ExchangeController {
    private static final Logger LOGGER = LoggerFactory.getLogger(RhoInterviewChallenge.class);
	
    private final ExchangeRateService exchangeRateService;

    @Autowired
    public ExchangeController(ExchangeRateService exchangeRateService) {
        this.exchangeRateService = exchangeRateService;
    }
	// Get exchange rates for a currency A to all or to a specific currency B
	// If currency parameter is not provided, return all exchange rates for currency
	// A in the path variable
	// If multiple currencies parameters are provided, return a list of exchange
	// rates to each currency
	// If the currency is not found, return an error

	@RateLimiter(name = "exchangeRateLimiter")
	@Operation(summary = "Get exchange rate for a currency", description = "Retrieves exchange rates for the given source currency.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Exchange rates successfully fetched"),
		@ApiResponse(responseCode = "404", description = "Currency not found")
	})
	@QueryMapping
	@GetMapping(value = "/rate/{source}")
	public ResponseEntity<Object> rate(
			@Parameter(name = "source", description = "Original currency", example = "USD") @Argument @PathVariable("source") String source,
			@Parameter(name = "target", description = "List of target currencies", example = "target=EUR&target=GGP") @Argument @RequestParam(required = false) List<String> target) {
		LOGGER.info("Getting exchange rate for currency " + source);
		
		// We can provision three options:
		// - If no target is provided, return all exchange rates for the source currency
		// - If one target is provided, return the exchange rate for the source currency to the target currency
		// - If multiple targets are provided, return a list of exchange rates for the source currency to each target currency

		ExchangeRate exchangeRate = exchangeRateService.getExchangeRate();
		Map<String, Double> rates = new HashMap<>();

		// Case 1: No target provided. Return all rates except source
		if (target == null || target.isEmpty()) {
			if (!exchangeRate.getRates().containsKey(source)) {
				return new ResponseEntity<>("Currency not found", HttpStatus.NOT_FOUND);
			}

			for (String currency : exchangeRate.getRates().keySet()) {
				if (!currency.equals(source)) {
					rates.put(currency, exchangeRate.getRate(source, currency));
				}
			}
			return new ResponseEntity<>(rates, HttpStatus.OK);
		}

		// Case 2: Single or multiple target currencies
		for (String currency : target) {
			double rate = exchangeRate.getRate(source, currency);
			if (rate == -1.0) {
				return new ResponseEntity<>("Currency not found", HttpStatus.NOT_FOUND);
			}
			rates.put(currency, rate);
		}
		return new ResponseEntity<>(rates, HttpStatus.OK);
	}

	// Get value conversion for a currency A to all or to a the value in currency B
	// If currency parameter is not provided, return all values in all currencies
	// If multiple currencies parameters are provided, return a list of values in
	// each currency
	// If the currency is not found, return an error

	@RateLimiter(name = "exchangeRateLimiter")
	@Operation(summary = "Get value conversion", description = "Converts a given amount from the source currency to one or multiple target currencies.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Value conversion successfully fetched"),
		@ApiResponse(responseCode = "400", description = "Invalid amount format"),
		@ApiResponse(responseCode = "404", description = "Currency not found")
	})
	@QueryMapping
	@GetMapping(value = "/value/{source}/{amount}")
	public ResponseEntity<Object> value(
			@Parameter(name = "source", description = "Original currency", example = "USD") @Argument @PathVariable("source") String source,
			@Parameter(name = "amount", description = "Amount to convert", example = "2.5") @Argument @PathVariable("amount") String amount,
			@Parameter(name = "target", description = "List of target currencies", example = "target=EUR&target=GGP") @Argument @RequestParam(required = false) List<String> target) {

		// We can provision three options:
		// - If no target is provided, return all values for the source currency
		// - If one target is provided, return the value in the target currency
		// - If multiple targets are provided, return a list of values in each target currency

		LOGGER.info("Fetching value conversion for source currency: {} with amount: {}", source, amount);
		ExchangeRate exchangeRate = exchangeRateService.getExchangeRate();
		double amountValue;

		try {
			amountValue = Double.parseDouble(amount);
		} catch (NumberFormatException e) {
			return new ResponseEntity<>("Invalid amount format", HttpStatus.BAD_REQUEST);
		}

		Map<String, Double> values = new HashMap<>();

		if (target == null) {
			if (!exchangeRate.getRates().containsKey(source)) {
				return new ResponseEntity<>("Currency not found", HttpStatus.NOT_FOUND);
			}
			for (String currency : exchangeRate.getRates().keySet()) {
				if (!currency.equals(source)) {
					values.put(currency, exchangeRate.getRate(source, currency) * amountValue);
				}
			}
			return new ResponseEntity<>(values, HttpStatus.OK);
		}

		for (String currency : target) {
			double rate = exchangeRate.getRate(source, currency);
			if (rate == -1.0) {
				return new ResponseEntity<>("Currency not found", HttpStatus.NOT_FOUND);
			}
			values.put(currency, rate * amountValue);
		}

		return new ResponseEntity<>(values, HttpStatus.OK);
	}
}
