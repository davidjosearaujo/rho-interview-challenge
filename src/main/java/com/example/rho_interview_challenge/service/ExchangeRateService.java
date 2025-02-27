package com.example.rho_interview_challenge.service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.rho_interview_challenge.ExchangeRate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class ExchangeRateService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExchangeRateService.class);
    private final String exchangeRateApiUrl;
    private ExchangeRate exchangeRate;
    private long lastUpdated = 0;

    @Value("${exchangerateapi.cache_duration}") 
    private int CACHE_DURATION;

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    public ExchangeRateService(@Value("${exchangerateapi.url}") String exchangeRateApiUrl) {
        this.exchangeRateApiUrl = exchangeRateApiUrl;
        this.exchangeRate = fetchExchangeRate();
    }

    public synchronized ExchangeRate getExchangeRate() {
        if (Instant.now().getEpochSecond() - lastUpdated > CACHE_DURATION) {
            LOGGER.warn("Exchange rate is obsolete. Fetching new rates...");
            this.exchangeRate = fetchExchangeRate();
            lastUpdated = Instant.now().getEpochSecond();
        }
        return exchangeRate;
    }

    private ExchangeRate fetchExchangeRate() {
        try {
            LOGGER.info("Fetching exchange rates...");
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(exchangeRateApiUrl))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                LOGGER.error("Failed to fetch exchange rates. HTTP Status: {}", response.statusCode());
                return new ExchangeRate(); // Return an empty object instead of throwing an exception
            }

            JsonNode rootNode = mapper.readTree(response.body());
            Map<String, Double> rates = new HashMap<>();
            rootNode.get("quotes").fields().forEachRemaining(entry -> {
                rates.put(entry.getKey().substring(3), entry.getValue().asDouble());
            });
            rates.put(rootNode.get("source").asText(), 1.0);

            LOGGER.info("Exchange rates successfully updated.");
            return new ExchangeRate(rootNode.get("source").asText(), rates);
        } catch (IOException | InterruptedException e) {
            LOGGER.error("Error fetching exchange rates", e);
            return new ExchangeRate(); // Prevent system crashes by returning an empty object
        }
    }
}
