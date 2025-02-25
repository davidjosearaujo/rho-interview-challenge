package com.example.rho_interview_challenge;

import java.util.HashMap;
import java.util.Map;

public class ExchangeRate {
    private String currency;
    private Map<String, Double> rates;

    public ExchangeRate() {
        this.currency = "USD";
        this.rates = new HashMap<>();
    }

    public ExchangeRate(String currency, Map<String, Double> rates) {
        this.currency = currency;
        this.rates = rates;
    }

    public Map<String, Double> getRates() {
        return rates;
    }

    public double getRate(String source, String target) {
        if (!rates.containsKey(source) || !rates.containsKey(target)) {
            return -1.0;
        }
        if (source.equals(currency)) {
            return rates.get(target);
        } else if (target.equals(currency)) {
            return 1 / rates.get(source);
        } else {
            return (1 / rates.get(source)) * (rates.get(target));
        }
    }
}
