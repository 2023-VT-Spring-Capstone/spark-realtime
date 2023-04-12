import yfinance as yf
import mysql.connector
import json
import datetime


def main():

    print("Run news.py")

    # Connect to MySQL database
    mydb = mysql.connector.connect(
        host="localhost",
        port=3307,
        user="root",
        password="root",
        database="yfinance"
    )

    # Create a cursor object
    cursor = mydb.cursor()

    sql = '''
        CREATE TABLE IF NOT EXISTS news (
            id INT AUTO_INCREMENT PRIMARY KEY,
            symbol VARCHAR(10) NOT NULL,
            uuid CHAR(36) NOT NULL UNIQUE,
            title VARCHAR(255) NOT NULL,
            publisher VARCHAR(60) NOT NULL,
            link VARCHAR(170) NOT NULL,
            publish_time DATETIME NOT NULL,
            news_type VARCHAR(20) NOT NULL,
            thumbnail JSON,
            related_tickers JSON,
            created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
        )
    '''

    cursor.execute(sql)

    # Stocks to fetch
    sql = "SELECT name FROM ticker ORDER BY id"
    cursor.execute(sql)

    tickers = []
    for (name,) in cursor:
        tickers.append(name)

    for ticker in tickers:
        news_data = yf.Ticker(ticker).news
        # print(news_data)
        parsed_news_data = json.loads(json.dumps(news_data))
        # print(parsed_news_data)
        for news in parsed_news_data:
            # print(news['uuid'])
            # print(news['title'])
            # print(news['publisher'])
            # print(news['link'])
            # print(news['providerPublishTime'])
            # print(news['type'])
            # print(news['thumbnail'])
            # print(news['relatedTickers'])
            sql = """
                INSERT IGNORE INTO news (symbol, uuid, title, publisher, link, publish_time, news_type, thumbnail, related_tickers)
                VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s)
                """
            publish_time = datetime.datetime.fromtimestamp(news['providerPublishTime'])
            # print(publish_time)
            thumbnail = json.dumps(news.get('thumbnail', None))
            related_tickers = json.dumps(news.get('relatedTickers', None))
            val = (ticker, news['uuid'], news['title'], news['publisher'], news['link'], publish_time, news['type'], thumbnail, related_tickers)
            cursor.execute(sql, val)

    # Commit changes to database and close connection
    mydb.commit()
    cursor.close()
    mydb.close()

    print("Finished running news.py")


if __name__ == '__main__':
    main()
