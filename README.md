# momentum-backtester
REST API that allows backtesting a momentum strategy performance on selected financial securities.

# What is the momentum strategy?
According to Wikipedia:
> Momentum investing is a system of buying stocks or other securities that have had high returns over the past three to twelve months, and selling those that have had poor returns over the same period.

An example of the strategy in practice:
You want to have either the S&P 500 ETF or US 10 Year Treasury ETF in your portfolio. No 60/40. Either 100% S&P 500 or 100% Bond.
Momentum tells you what you should hold in your portfolio at the moment.

1. The first thing that you do is you will compare the S&P 500 vs. US Bond over the past 12 months to see which has performed better.
2. You add the asset with better performance to your portfolio.
3. You repeat the first step every month.
4. If your portfolio contains the asset that has performed better over the past 12 months, you will do nothing for the next month. Otherwise, you will sell the current asset and buy the one which has performed better.
5. You are a long term investor and repeat above steps for many years :) You will probably beat the market :)

# How does the momentum-backtester work?
The app stores the price history of several indexes (i.e. MSCI World, MSCI Emerging Markets, S&P 500, Gold spot price). You can fetch the ETF list that represents those funds by sending an HTTP request:
`GET /momentum/backtester/securities/etf`
The example output is:
```
[
    {
        "description": "iShares Physical Gold ETC",
        "ticker": "IGLN.UK",
        "dataFrom": "1978-11",
        "dataTo": "2021-06"
    },
    {
        "description": "iShares Edge MSCI World Momentum Factor UCITS ETF (Acc)",
        "ticker": "IWMO.UK",
        "dataFrom": "1994-05",
        "dataTo": "2021-06"
    },
    ....
]
```
Then you can choose couple of tickers that you want to backtest the strategy against and send the HTTP request:
`localhost:8080/momentum/backtester/securities/etf/tester/?from=1996-01&to=2021-12&chart=true`
with headers:
`tickers=EIMI.UK,CSPX.UK` and `momentumMonths=12`.

It will test the performance of the strategy against ETFs with tickers provided in headers from given date and will draw a net asset value over time chart.
Example output:
```
{
    "testedTickers": [
        "EIMI.UK",
        "CSPX.UK"
    ],
    "amountInvestedInEur": 10000,
    "firstMonth": "1996-01",
    "lastMonth": "2021-06",
    "noOfTransactions": 24,
    "netAssetValueInEur": 215212.736899,
    "cagr": 12.879014421049062
}
```




