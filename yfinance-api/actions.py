import yfinance as yf
import mysql.connector
from db_config import db_config


def main():

    print("Run actions.py")

    # Connect to MySQL database
    mydb = mysql.connector.connect(**db_config)

    # Create a cursor object
    cursor = mydb.cursor()

    sql = '''
        CREATE TABLE IF NOT EXISTS actions (
            id INT AUTO_INCREMENT PRIMARY KEY,
            symbol VARCHAR(10) NOT NULL,
            action_date DATE NOT NULL,
            dividends DOUBLE NOT NULL,
            stock_splits DOUBLE NOT NULL,
            created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
            UNIQUE (symbol, action_date)
        )
    '''

    cursor.execute(sql)

    # stocks to fetch
    sql = "SELECT name FROM ticker ORDER BY id"
    cursor.execute(sql)

    tickers = []
    for (name,) in cursor:
        tickers.append(name)

    for ticker in tickers:
        actions = yf.Ticker(ticker).actions
        # print("actions = ", actions)
        # print("type(actions) = ", type(actions))
        for index, row in actions.iterrows():
            sql = """
                INSERT IGNORE INTO actions (symbol, action_date, dividends, stock_splits)
                VALUES (%s, %s, %s, %s)
                """
            val = (ticker, index.strftime('%Y-%m-%d'), row['Dividends'], row['Stock Splits'])
            cursor.execute(sql, val)

    # Commit changes to database and close connection
    mydb.commit()
    cursor.close()
    mydb.close()

    print("Finished running actions.py")


if __name__ == '__main__':
    main()
