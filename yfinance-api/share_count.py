import yfinance as yf
import mysql.connector


def main():

    print("Run share_count.py")

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
        CREATE TABLE IF NOT EXISTS share_count (
            id INT AUTO_INCREMENT PRIMARY KEY,
            symbol VARCHAR(10) NOT NULL,
            record_date DATE NOT NULL,
            share_count BIGINT NOT NULL,
            created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
            UNIQUE (symbol, record_date)
        )
    '''

    cursor.execute(sql)

    sql = '''
            CREATE TABLE IF NOT EXISTS params (
                id INT AUTO_INCREMENT PRIMARY KEY,
                api_name VARCHAR(20) NOT NULL,
                param_name VARCHAR(20) NOT NULL,
                param_value VARCHAR(10) NOT NULL,
                UNIQUE(api_name, param_name)
            );
        '''

    cursor.execute(sql)

    _API_NAME = 'get_shares_full'

    cursor.execute("INSERT IGNORE INTO params (api_name, param_name, param_value) VALUES ('{}', 'start', '2021-01-01')".format(_API_NAME))

    cursor.execute("SELECT param_value FROM params WHERE api_name = '{}' AND param_name = 'start'".format(_API_NAME))
    start = cursor.fetchone()[0]

    # Stocks to fetch
    sql = "SELECT name FROM ticker ORDER BY id"
    cursor.execute(sql)

    tickers = []
    for (name,) in cursor:
        tickers.append(name)

    for ticker in tickers:
        share_count = yf.Ticker(ticker).get_shares_full(start=start)
        # print(share_count)
        for index, value in share_count.items():
            sql = """
                INSERT IGNORE INTO share_count (symbol, record_date, share_count)
                VALUES (%s, %s, %s)
                """
            val = (ticker, index, value)
            cursor.execute(sql, val)

    # Commit changes to database and close connection
    mydb.commit()
    cursor.close()
    mydb.close()

    print("Finished running share_count.py")


if __name__ == '__main__':
    main()
