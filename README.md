# Rho Interview Challenge

# How to run 

```bash
docker compose up --build
```

> **ATTENTION**
> In order to interact with the data provider API you need an **api key**.\
> Also, there are **environment variable that need to be set**! This can be done by creating a `.env` file in the same directory of the `docker-compose.yml`

The `.env` file **needs** to contains these variables:
```bash
EXCHANGE_RATE_KEY=<api-key>
CACHE_DURATION=60

LIMIT_FOR_PERIOD=5
LIMIT_REFRESH_PERIOD=60
TIMEOUT_DURATION=60

KEYSTORE_PASSWORD=rhointerview

# ATTENTION: These keys are for testing purposes only, DO NOT USE THEN IM PRODUCTION!
ACCESS_TOKEN_PRIVATE_KEY_PATH=access-refresh-token-keys/access-token-private.key
ACCESS_TOKEN_PUBLIC_KEY_PATH=access-refresh-token-keys/access-token-public.key
REFRESH_TOKEN_PRIVATE_KEY_PATH=access-refresh-token-keys/refresh-token-private.key
REFRESH_TOKEN_PUBLIC_KEY_PATH=access-refresh-token-keys/refresh-token-public.key

# MongoDB variables are defined in docker compose file
```

# Design process

## Important notes

> *"make as few calls as possible"*

We can **use "two-step conversion"** for exchanges by query only for the rates of exchange for currency X to all other currency and then use it as a conversion map. **This way we can only query for complete list of rates and only when data is obsolete.**

> ### Example:
>
> - If we are first queried for the rate between A and B, we can request the provider for all the rates of A, and if we are then queried for X to Z, we can just use the existing locally stored rates (if not obsolete) and calculate X to A and A to Z.
> - **Exchange rate of A to B = (A to X) * (X to B)**

## API 

### Interaction flowchart

Minimal steps approach, while making as few calls to data provider as possible.

```mermaid
flowchart LR
    step1(Receive client call) --> step2{Are there stored rates}
    step2 -- Yes --> step3{Older than 1 min?}
    step3 -- Yes --> step4(Query data provider)
    step3 -- No --> step6(Response)
    step2 -- No --> step4
    step4 --> step5(Store exchange rates)
    step5 --> step6
```

### Endpoints

![endpoints](docs/Screenshot from 2025-02-27 16-25-24.png)

- `/api/exchange/rate/A` - Get exchange rates for currency A
- `/api/exchange/rate/A?currency=B` - Get exchange rate from currency A to B
- `/api/exchange/rate/A?c=B&c=C&c=D` - Get exchange rate from currency A to B, C and D
- `/api/exchange/value/A?c=B&v=3.0` - Get A value in B currency
- `/api/exchange/value/A?c=B&c=C&c=D&v=3.0` - Get A value in B, C and D currency
- `/api/register` - Register new user
- `/api/login` - Login user
- `/api/token` - Refresh token
- `/swagger-ui/index.html` - API Swagger Docs

## Testing

[Here](docs/Rho%20Interview%20Challenge.postman_collection.json) you can find a Postman collection you can use to test all endpoints. Complete with variables for the tokens.

# Data provider

## Response format

For this request: `https://api.exchangerate.host/live?access_key=<api_key>`

We get the following response

```json
{
 "success": true,
 "terms": "https:\/\/currencylayer.com\/terms",
 "privacy": "https:\/\/currencylayer.com\/privacy",
 "timestamp": 1740477123,
 "source": "USD",
 "quotes": {
  "USDAED": 3.672745,
  "USDAFN": 73.978873,
  ...
 }
}
```

# Feature implementation

- [x] Get exchange from A to B
- [x] Get exchange from A to B..Z
- [x] Get value from A to B
- [x] Get value from A to list of provided currencies
- [x] Auto documentation with Swagger
- [x] Support for GraphQL
- [x] Implement rate limiting
- [x] Dockerize
- [x] SSL
- [x] Implement authentication/authorization
- [x] CORS
- [x] E2E API testing
- [ ] Unit testing
- [x] Remove TESTING blocks (I exceeded the monthly API calls to the provider hahaha)