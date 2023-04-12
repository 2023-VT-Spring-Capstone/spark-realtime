import yfinance as yf
import mysql.connector
import pandas as pd


def main():

    print("Run earnings.py")

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
        CREATE TABLE IF NOT EXISTS earnings (
            id INT AUTO_INCREMENT PRIMARY KEY,
            symbol VARCHAR(10) NOT NULL,
            earnings_date DATETIME NOT NULL,
            eps_estimate FLOAT,
            reported_eps FLOAT,
            surprise_pct DOUBLE(7,4),
            created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
            updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
            UNIQUE (symbol, earnings_date)
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
        # print("ticker =", ticker)
        earnings = yf.Ticker(ticker).earnings_dates
        # print(earnings)
        for index, row in earnings.iterrows():
            sql = """
                INSERT INTO earnings (symbol, earnings_date, eps_estimate, reported_eps, surprise_pct)
                VALUES (%s, %s, %s, %s, %s)
                ON DUPLICATE KEY UPDATE 
                        eps_estimate = VALUES(eps_estimate), 
                        reported_eps = VALUES(reported_eps), 
                        surprise_pct = VALUES(surprise_pct)
                """
            val = (ticker, index, row['EPS Estimate'], row['Reported EPS'], row['Surprise(%)'])
            val = tuple(x if not pd.isna(x) else None for x in val)
            cursor.execute(sql, val)

    # Commit changes to database and close connection
    mydb.commit()
    cursor.close()
    mydb.close()

    print("Finished running earnings.py")


if __name__ == '__main__':
    main()
