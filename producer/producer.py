import praw
from kafka import KafkaProducer
import threading
import json
import time

reddit = praw.Reddit(
    client_id='rl5NKpg-FlnwfaEqRDazsA',
    client_secret='9N-oaVh3FvKdCouv2CJPy-XrYjaiTg',
    user_agent='kafka-producer',
    username='huaiche94',
    password='Roy830120*')

# Kafka Producer configuration
kafka_config = {
    'bootstrap_servers': ['localhost:9092', 'localhost:9093', 'localhost:9094']
}

# Kafka Topic
AMC_TOPIC = 'AMC_TOPIC'
GME_TOPIC = 'GME_TOPIC'
TSLA_TOPIC = "TSLA_TOPIC"

# Create Kafka Producer instance
kafka_producer = KafkaProducer(
    value_serializer=lambda x: json.dumps(x).encode("utf-8"),
    bootstrap_servers=kafka_config['bootstrap_servers']
)

stock_market_subreddits = ["StockMarket",
                           "stocks",
                           "investing",
                           "wallstreetbets",
                           "options",
                           "SecurityAnalysis",
                           "ValueInvesting",
                           "Daytrading",
                           "Stock_Picks",
                           "StocksAndTrading",
                           "pennystocks",
                           "CanadianInvestor",
                           "economy",
                           "finance",
                           "InvestmentClub",
                           "MemeEconomy",
                           "StocksAndBoobs"]

amc_subreddits = ["amc", "amcstock", "AMCSTOCKS"]
gme_subreddits = [
    "GME",
    "GMEJungle",
    "gme_meltdown",
    "gmeamcstonks",
    "gme_capitalists"
]

tsla_subreddits = [
    "Tesla",
    "teslainvestorsclub",
    "RealTesla",
    "teslamotors",
    "SpaceX",
    "elonmusk",
    "electricvehicles",
    "investing",
    "stocks",
    "wallstreetbets"
]


print("Listening for new posts:")


# Single Thread on multiple subreddits
# while True:
#     try:
#         for subreddit_name in gme_subreddits:
#             subreddit = reddit.subreddit(subreddit_name)
#             for post in subreddit.stream.submissions():
#                 message = {
#                     'id': post.id,
#                     'title': post.title,
#                     'body': post.selftext,
#                     'score': post.score,
#                     'num_comments': post.num_comments,
#                     'url': post.url,
#                     'subreddit': subreddit.display_name
#                 }
#                 kafka_producer.send(kafka_topic, value=message)
#                 print("New Post sent to Kafka:", message)
#                 print()
#                 kafka_producer.flush()
#     except Exception as e:
#         print(e)
#         time.sleep(60)

# Multi-thread on multiple subreddits: AMC/GME/TSLA
# Define function to handle subreddit updates
def handle_subreddit_updates(subreddit_names, topic):
    while True:
        for subreddit_name in subreddit_names:
            subreddit = reddit.subreddit(subreddit_name)
            for submission in subreddit.stream.submissions():
                post_data = {
                    "id": submission.id,
                    "title": submission.title,
                    "body": submission.selftext,
                    "score": submission.score,
                    "num_comments": submission.num_comments,
                    "url": submission.url,
                    'subreddit': subreddit.display_name
                }
                kafka_producer.send(topic, value=post_data)


# Start threads to handle subreddit updates
amc_thread = threading.Thread(target=handle_subreddit_updates, args=(amc_subreddits, AMC_TOPIC))
gme_thread = threading.Thread(target=handle_subreddit_updates, args=(gme_subreddits, GME_TOPIC))
tsla_thread = threading.Thread(target=handle_subreddit_updates, args=(tsla_subreddits, TSLA_TOPIC))
amc_thread.start()
gme_thread.start()
tsla_thread.start()

# Wait for threads to complete
amc_thread.join()
gme_thread.join()
tsla_thread.join()
