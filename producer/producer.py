import json
import threading
import time
from datetime import datetime
import praw
import pytz
import redis
from kafka import KafkaProducer


local_timezone = pytz.timezone('America/New_York')
# Set up Reddit API client
# FILL IN username, password of yours
reddit = praw.Reddit(
    client_id='rl5NKpg-FlnwfaEqRDazsA',
    client_secret='9N-oaVh3FvKdCouv2CJPy-XrYjaiTg',
    user_agent='kafka-producer',
    username='',
    password='')

# Kafka Producer configuration
kafka_config = {
    'bootstrap_servers': ['localhost:9092', 'localhost:9093', 'localhost:9094']
}
# Set up Redis client
redis_client = redis.Redis(host='localhost', port=6379)

# Create Kafka Producer instance
producer = KafkaProducer(
    value_serializer=lambda x: json.dumps(x).encode("utf-8"),
    bootstrap_servers=kafka_config['bootstrap_servers']
)

stock_market_subreddits = ["stocks",
                           "StockMarket",
                           "Stock_Picks",
                           "StocksAndTrading",
                           "Shortsqueeze",
                           "investing", "Daytrading",
                           "wallstreetbets", "pennystocks", "Robinhood", "RobinHoodPennyStocks",
                           "options",
                           "SecurityAnalysis"
                           "ValueInvesting",
                           "CanadianInvestor",
                           "economy",
                           "finance",
                           "InvestmentClub",
                           "MemeEconomy",
                           "StocksAndBoobs"
                           ]

subreddits = [
    {
        "symbol": "AMC",
        "subreddit_names": ["amc", "amcstock", "AMCSTOCKS"]
    },
    {
        "symbol": "BB",
        "subreddit_names": ["BB_stock", "BlackBerryStock", "CanadianInvestor"]
    },
    {
        "symbol": "BBBY",
        "subreddit_names": ["BBBY", "bbby_remastered"]
    },
    {
        "symbol": "GME",
        "subreddit_names": ["GME", "GMEJungle", "gme_meltdown", "gmeamcstonks", "gme_capitalists"]
    },
    {
        "symbol": "TSLA",
        "subreddit_names": ["Tesla", "teslainvestorsclub", "RealTesla", "teslamotors", "SpaceX", "elonmusk",
                            "electricvehicles", "investing", "stocks", "wallstreetbets"]
    },
    # Add more subreddits here as needed
]
print("Listening for new posts:")


# Define function to send post data to Kafka topic
# ID of post is checked here to prevent duplicate posts sent to Kafka
def send_to_kafka(topic, post, subreddit_name, ticker, label):
    # Check if post ID has already been sent, so we won't send duplicate posts into kafka
    if not redis_client.get(post.id):
        # Add post ID to Redis with 1 hour expiration
        redis_client.set(post.id, 1, ex=3600)
        # Send post data to Kafka topic
        post_data = {
            "author": post.author,
            "author_flair_text": post.author_flair_text,
            "created_time": post.created_utc,
            "id": post.id,
            "is_original": post.is_original_content,
            "is_self": post.is_self,
            "permalink": post.permalink,
            "title": post.title,
            "body": post.selftext,
            "score": post.score,
            "upvote_ratio": post.upvote_ratio,
            "num_comments": post.num_comments,
            "url": post.url,
            "subreddit": subreddit_name,
            "ticker": ticker,
            "label": label,
            "sentiment": None,
        }
        if post_data['author'] is not None:
            post_data['author'] = post_data['author'].name

        local_time = datetime.utcfromtimestamp(post_data['created_time']).replace(tzinfo=pytz.utc, microsecond=0).astimezone(local_timezone)
        post_data['created_time'] = local_time.strftime('%Y-%m-%d %H:%M:%S')

        producer.send(topic, value=post_data)


# Define function to fetch and send top/hot/controversial posts
# DWD_TOP_LOG, DWD_HOT_LOG, DWD_CONTROVERSIAL_LOG
def fetch_and_send_posts(subreddit_name_list, ticker):
    for subreddit_name in subreddit_name_list:
        sub = reddit.subreddit(subreddit_name)
        # Fetch and send top posts
        for post in sub.top(limit=10):
            send_to_kafka('DWD_BASE_LOG', post, subreddit_name, ticker, "top")
        # Fetch and send hot posts
        for post in sub.hot(limit=10):
            send_to_kafka('DWD_BASE_LOG', post, subreddit_name, ticker, "hot")
        # Fetch and send controversial posts
        for post in sub.controversial(limit=10):
            send_to_kafka('DWD_BASE_LOG', post, subreddit_name, ticker, "controversial")


# Define function to listen to active user count
def listen_active_user_count(subreddit_name_list, ticker):
    active_user_count = 0
    for subreddit_name in subreddit_name_list:
        sub = reddit.subreddit(subreddit_name)
        active_user_count += sub.active_user_count
    print(f"{ticker}: Active user count: {active_user_count}")


def listen_new_posts(subreddit_name_list, ticker):
    for subreddit_name in subreddit_name_list:
        sub = reddit.subreddit(subreddit_name)
        for post in sub.new(limit=50):
            # Get post's creation timestamp
            post_created_utc = datetime.utcfromtimestamp(post.created_utc)
            # Get current date in UTC timezone
            current_utc_time = datetime.utcnow().replace(tzinfo=pytz.utc)
            # Compare post's creation timestamp with current date
            if post_created_utc.date() == current_utc_time.date():
                send_to_kafka('DWD_BASE_LOG', post, subreddit_name, ticker, "new")

# Update hot/top/controversial posts
def htc_type_handler():
    threads = []

    for sub in subreddits:
        sym = sub["symbol"]
        sub_names = sub["subreddit_names"]
        htc_post_thread = threading.Thread(target=fetch_and_send_posts, args=(sub_names, sym))
        htc_post_thread.start()
        threads.append(htc_post_thread)

    for th in threads:
        th.join()

# Listen on 1.new posts and 2.active user counts
def new_active_handler():
    threads_realtime = []

    for subreddit in subreddits:
        symbol = subreddit["symbol"]
        subreddit_names = subreddit["subreddit_names"]

        listen_new_post_thread = threading.Thread(target=listen_new_posts, args=(subreddit_names, symbol))

        listen_active_user_count_thread = threading.Thread(target=listen_active_user_count,
                                                           args=(subreddit_names, symbol))

        listen_new_post_thread.start()
        listen_active_user_count_thread.start()

        threads_realtime.append(listen_new_post_thread)
        threads_realtime.append(listen_active_user_count_thread)

    for thread in threads_realtime:
        thread.join()


def main():
    # might need to use different threads in the future. Now new_active_handler is blocked by htc_type_handler
    # htc_type_handler()
    # TODO: execute yahoo_finance here
    # yahoo_finance.execute()
    while True:
        new_active_handler()
        print("Current Time is: ", datetime.now())
        time.sleep(10)


if __name__ == "__main__":
    main()
