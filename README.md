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
|name|attribute|description|
|---|---|---|
|||
