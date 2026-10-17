# Fetched exchange rate suggests; the received LKR amount decides

Kavindu's AdSense (USD) and crypto (USDT/ETH) income is converted at whatever rate his bank or exchange actually applied on the day of transfer, which is usually below the market rate. The app fetches USD→LKR daily from open.er-api.com and USDT/ETH→USD from CoinGecko, caches the last value for offline use, and pre-fills it on the income form — but the user may instead enter the LKR amount actually credited, from which the effective rate is derived. The rate stored on each income entry is frozen at save time and never recomputed, so past months always reconcile with his bank balance.

## Considered Options

- **Live rate only, recomputed on display**: rejected — historic months would silently change and drift from his bank statement, the same loss of trust that ended the Google Sheet.
- **Manual rate only, no fetch**: rejected — adds a lookup step to every foreign-currency entry, raising logging friction.
