# spark-streaming 

![capstone system architecture](https://user-images.githubusercontent.com/14934562/223332431-e86c2c5d-5dc1-405a-b471-e5aa2d5f92df.png)


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


### Yahoo finance data
reference: https://pypi.org/project/yfinance/

The historical market data of a specified stock:

|name|attribute|description|
|---|---|---|
|"trade_date"|history.Date|The date of a particular trading day.|
|"open"|history.Open|The opening price of the stock for the trading day.|
|"high"|history.High|The highest price the stock reached during the trading day.|
|"low"|history.Low|The lowest price the stock reached during the trading day.|
|"close"|history.Close|The closing price of the stock for the trading day.|
|"volume"|history.Volume|The total number of shares traded during the trading day.|

The corporate actions taken by the company of the specified stock:

|name|attribute|description|
|---|---|---|
|"action_date"|actions.Date|The date on which the corporate action occurred.|
|"dividends"|actions.Dividends|The dividend payment(s) made by the company on the given date.|
|"stock_splits"|actions.Stock Splits|The stock split(s) that occurred on the given date, expressed as a ratio (e.g. "2:1")|

The snapshot of the current state of a specified stock:

|name|attribute|description|
|---|---|---|
|"currency"|fast_info.currency|The currency in which the financial instrument is priced.|
|"day_high"|fast_info.dayHigh|The highest price of the day for the financial instrument.|
|"day_low"|fast_info.dayLow|The lowest price of the day for the financial instrument.|
|"exchange"|fast_info.exchange|The exchange on which the financial instrument is listed.|
|"fifty_day_average"|fast_info.fiftyDayAverage|The average price of the financial instrument over the last fifty days.|
|"last_price"|fast_info.lastPrice|The last traded price of the financial instrument.|
|"last_volume"|fast_info.lastVolume|The volume of the last trade for the financial instrument.|
|"market_cap"|fast_info.marketCap|The market capitalization of the financial instrument.|
|"day_open"|fast_info.open|The opening price of the financial instrument for the day.|
|"pre_close"|fast_info.previousClose|The previous day's closing price of the financial instrument.|
|"quote_type"|fast_info.quoteType|The type of financial instrument.|
|"reg_pre_close"|fast_info.regularMarketPreviousClose|The previous regular trading hours closing price of the financial instrument.|
|"num_shares"|fast_info.shares|The number of shares outstanding for the financial instrument.|
|"ten_day_aver_vol"|fast_info.tenDayAverageVolume|The ten-day average volume for the financial instrument.|
|"three_month_aver_vol"|fast_info.threeMonthAverageVolume|The three-month average volume for the financial instrument.|
|"timezone"|fast_info.timezone|The timezone of the exchange where the financial instrument is listed.|
|"two_hundred_day_aver"|fast_info.twoHundredDayAverage|The two-hundred-day average price of the financial instrument.|
|"year_change"|fast_info.yearChange|The change in the price of the financial instrument over the year.|
|"year_high"|fast_info.yearHigh|The highest price of the financial instrument over the year.|
|"year_low"|fast_info.yearLow|The lowest price of the financial instrument over the year.|


