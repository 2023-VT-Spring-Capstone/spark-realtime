# spark-streaming 

![capstone system architecture](https://user-images.githubusercontent.com/14934562/223332431-e86c2c5d-5dc1-405a-b471-e5aa2d5f92df.png)

# Reddit
### Subreddits - general
1. r/StockMarket: This subreddit is dedicated to discussions about the stock market, investing, and trading.
2. r/Investing: This subreddit is focused on investment-related discussions and analysis.
3. r/Daytrading: This subreddit is for discussions about day trading and short-term trading strategies.
4. r/WallStreetBets: This subreddit gained popularity in recent years due to its focus on high-risk, high-reward trades and investments.
5. r/Stocks: This subreddit is a general forum for stock market discussions, news, and analysis.
6. r/Options: This subreddit is focused on options trading and strategies.
7. r/PennyStocks: This subreddit is dedicated to discussions about penny stocks, which are stocks that trade for less than $5 per share.
8. r/Robinhood: This subreddit is focused on discussions about the Robinhood trading platform and related topics.
9. r/SecurityAnalysis: This subreddit is dedicated to discussions about security analysis and valuation.

### Subreddits - targeted (WIP)
|name|ticker symbol|subreddits|
|---|---|---|
|AMC|AMC|"amc", "amcstock", "AMCSTOCKS"|
|GameStop|GME|"GME", "GMEJungle", "gme_meltdown", "gmeamcstonks", "gme_capitalists"|
|Tesla|TSLA|"Tesla", "teslainvestorsclub", "RealTesla", "teslamotors", "SpaceX", "elonmusk", "electricvehicles", "investing", "stocks", "wallstreetbets"|

### Reddit stream data
reference: https://praw.readthedocs.io/en/stable/code_overview/models/submission.html#praw.models.Submission

|name|attribute|description|
|---|---|---|
|"author"| submission.author|author id|
|"author_flair_text"| submission.author_flair_text|The text content of the author’s flair, or None if not flaired.|
|"created_time"| datetime.utcfromtimestamp(submission.created_utc).strftime('%Y-%m-%dT%H:%M:%S.%fZ')|Time the submission was created, represented in Unix Time.|
|"id"| submission.id|ID of the submission.|
|"is_original"| submission.is_original_content|Whether or not the submission has been set as original content.|
|"is_self"| submission.is_self|Whether or not the submission is a selfpost (text-only).|
|"permalink"| submission.permalink|A permalink for the submission.|
|"title"| submission.title|The title of the submission.|
|"body"| submission.selftext|The submissions’ selftext - an empty string if a link post. body of the post if it's a self post|
|"score"| submission.score|The number of upvotes for the submission.|
|"upvote_ratio"| submission.upvote_ratio|The percentage of upvotes from all votes on the submission.|
|"num_comments"| submission.num_comments|The number of comments on the submission.|
|"url"| submission.url|The URL the submission links to, or the permalink if a selfpost.|
|'subreddit'| subreddit.display_name|Provides an instance of Subreddit.|

# Yahoo finance
### Yahoo finance data
reference: https://pypi.org/project/yfinance/

The historical market data of a given stock:
```
yfinance.Ticker("SYMBOL").history(period="max") # period="1mo"
```
|name|attribute|description|
|---|---|---|
|"trade_date"|history.Date|The date of a particular trading day.|
|"open"|history.Open|The opening price of the stock for the trading day.|
|"high"|history.High|The highest price the stock reached during the trading day.|
|"low"|history.Low|The lowest price the stock reached during the trading day.|
|"close"|history.Close|The closing price of the stock for the trading day.|
|"volume"|history.Volume|The total number of shares traded during the trading day.|

The corporate actions taken by the company of a given stock:
```
yfinance.Ticker("SYMBOL").actions
```
|name|attribute|description|
|---|---|---|
|"action_date"|actions.Date|The date on which the corporate action occurred.|
|"dividends"|actions.Dividends|The dividend payment(s) made by the company on the given date.|
|"stock_splits"|actions.Stock Splits|The stock split(s) that occurred on the given date, expressed as a ratio (e.g. "2:1")|

The snapshot of the current state of a given stock:
```
yfinance.Ticker("SYMBOL").fast_info
```
|name|attribute|description|
|---|---|---|
|"currency"|fast_info.currency|The currency in which the stock is priced.|
|"day_high"|fast_info.dayHigh|The highest price of the day for the stock.|
|"day_low"|fast_info.dayLow|The lowest price of the day for the stock.|
|"exchange"|fast_info.exchange|The exchange on which the stock is listed.|
|"fifty_day_average"|fast_info.fiftyDayAverage|The average price of the stock over the last fifty days.|
|"last_price"|fast_info.lastPrice|The last traded price of the stock.|
|"last_volume"|fast_info.lastVolume|The volume of the last trade for the stock.|
|"market_cap"|fast_info.marketCap|The market capitalization of the stock.|
|"day_open"|fast_info.open|The opening price of the stock for the day.|
|"pre_close"|fast_info.previousClose|The previous day's closing price of the stock.|
|"quote_type"|fast_info.quoteType|The type of stock.|
|"reg_pre_close"|fast_info.regularMarketPreviousClose|The previous regular trading hours closing price of the stock.|
|"num_shares"|fast_info.shares|The number of shares outstanding for the stock.|
|"ten_day_aver_vol"|fast_info.tenDayAverageVolume|The ten-day average volume for the stock.|
|"three_month_aver_vol"|fast_info.threeMonthAverageVolume|The three-month average volume for the stock.|
|"timezone"|fast_info.timezone|The timezone of the exchange where the stock is listed.|
|"two_hundred_day_aver"|fast_info.twoHundredDayAverage|The two-hundred-day average price of the stock.|
|"year_change"|fast_info.yearChange|The change in the price of the stock over the year.|
|"year_high"|fast_info.yearHigh|The highest price of the stock over the year.|
|"year_low"|fast_info.yearLow|The lowest price of the stock over the year.|

The history meta data of a given stock:
```
yfinance.Ticker("SYMBOL").history_metadata
```
|name|attribute|description|
|---|---|---|
|currency|history_metadata.currency|The currency in which the stock is priced.|
|symbol|history_metadata.symbol|The ticker symbol for the stock.|
|exchange_name|history_metadata.exchangeName|The name of the exchange on which the stock is traded.|
|instrument_type|history_metadata.instrumentType|The type of stock (e.g., common stock, preferred stock, ETF, etc.).|
|first_trade_date|history_metadata.firstTradeDate|The date and time of the first trade for the stock.|
|regular_market_time|history_metadata.regularMarketTime|The date and time of the most recent trade for the stock.|
|gmt_offset|history_metadata.gmtoffset|The GMT offset for the time zone in which the stock is traded.|
|timezone|history_metadata.timezone|The time zone in which the stock is traded.|
|exchange_tz_name|history_metadata.exchangeTimezoneName|The name of the time zone in which the stock is traded.|
|reg_market_price|history_metadata.regularMarketPrice|The price of the stock at the most recent trade.|
|chart_prev_close|history_metadata.chartPreviousClose|The previous close price for the stock.|
|price_hint|history_metadata.priceHint|A hint for the number of decimal places to display when showing prices.|
|curr_pre_market_start|history_metadata.currentTradingPeriod|The start trading time for the pre-market.|
|curr_pre_market_end|history_metadata.currentTradingPeriod|The end trading time for the pre-market.|
|curr_reg_market_start|history_metadata.currentTradingPeriod|The start trading time for the regular market.|
|curr_reg_market_end|history_metadata.currentTradingPeriod|The end trading time for the regular market.|
|curr_post_market_start|history_metadata.currentTradingPeriod|The start trading time for the post-market.|
|curr_post_market_end|history_metadata.currentTradingPeriod|The end trading time for the post-market.|
|data_granularity|history_metadata.dataGranularity|The granularity of the historical data for the stock.|
|range|history_metadata.range|The time range for the historical data for the stock.|
|valid_ranges|history_metadata.validRanges|A list of valid time ranges for the historical data for the stock.|

The historical share count data of a given stock:

|name|attribute|description|
|---|---|---|
|date|N/A|The date and time.|
|share_count|N/A|The number of outstanding shares of the stock at the given date and time.|

The major holders data of a given stock:
```
yfinance.Ticker("SYMBOL").major_holders
```
|name|attribute|description|
|---|---|---|
|insider_hold_pct|N/A|Percentage of shares held by all insiders.|
|inst_hold_pct|N/A|Percentage of shares held by institutional investors.|
|float_hold_pct|N/A|Percentage of shares held by institutions as a percentage of the float.|
|num_inst|N/A|The number of institutional holders holding shares.|

The institutional holders data of a given stock:
```
yfinance.Ticker("SYMBOL").institutional_holders
```
|name|attribute|description|
|---|---|---|
|holder|institutional_holders.Holder|Name of the institutional holder.|
|shares|institutional_holders.Shares|Number of shares held by the institutional holder.|
|date_reported|institutional_holders.Date Reported|Date of the latest reported holding by the institutional holder.|
|pct_out|institutional_holders.% Out|Percentage of the total outstanding shares held by the institutional holder.|
|value|institutional_holders.Value|Total value of the shares held by the institutional holder.|

The mutual fund holders data of a given stock:
```
yfinance.Ticker("SYMBOL").mutualfund_holders
```
|name|attribute|description|
|---|---|---|
|holder|mutualfund_holders.Holder|Name of the mutual fund holder.|
|shares|mutualfund_holders.Shares|Number of shares held by the mutual fund holder.|
|date_reported|mutualfund_holders.Date Reported|Date of the latest reported holding by the mutual fund holder.|
|pct_out|mutualfund_holders.% Out|Percentage of the total outstanding shares held by the mutual fund holder.|
|value|mutualfund_holders.Value|Total value of the shares held by the mutual fund holder.|

The earnings data of a given stock company:
```
yfinance.Ticker("SYMBOL").earnings_dates
```
|name|attribute|description|
|---|---|---|
|earnings_date|earnings_dates.Earnings Date|Date and time of the earnings report.|
|eps_estimate|earnings_dates.EPS Estimate|Estimated earnings per share.|
|reported_eps|earnings_dates.Reported EPS|Actual reported earnings per share.|
|surprise_pct|earnings_dates.Surprise(%)|Percentage difference between EPS estimate and reported EPS.|

Related News of this company:
```
yfinance.Ticker("SYMBOL").news
```
|attribute|example|
|---|---|
|uuid | c2aa419c-bdb6-310f-80cf-d6e1fe2c2acd|
|title|Dow Jones Futures: Will Powell Testimony Threaten Stock Market Rally? Apple, Tesla In Focus|
|publisher|Investor's Business Daily|
|link| https://finance.yahoo.com/m/c2aa419c-bdb6-310f-80cf-d6e1fe2c2acd/dow-jones-futures%3A-will.html|
providerPublishTime| 1678193575|
|type|STORY|
|thumbnail|pictures url and size|
|relatedTickers|['TSLA', '^DJI', 'AYX', 'ANET', 'MSFT']|
