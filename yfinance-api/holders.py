import yfinance as yf
import mysql.connector


def main():

    print("Run holders.py")

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

    # Define the SQL query to create the stocks table
    sql = '''
        CREATE TABLE IF NOT EXISTS major_holders (
            id INT AUTO_INCREMENT PRIMARY KEY,
            symbol VARCHAR(10) NOT NULL,
            insider_hold_pct VARCHAR(8) NOT NULL,
            inst_hold_pct VARCHAR(8) NOT NULL,
            float_hold_pct VARCHAR(8) NOT NULL,
            num_inst INT NOT NULL,
            created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
            UNIQUE(symbol, insider_hold_pct, inst_hold_pct, float_hold_pct, num_inst)
        )
    '''

    cursor.execute(sql)

    sql = '''
        CREATE TABLE IF NOT EXISTS inst_holders (
            id INT AUTO_INCREMENT PRIMARY KEY,
            symbol VARCHAR(10) NOT NULL,
            holders VARCHAR(60) NOT NULL,
            shares BIGINT NOT NULL,
            date_reported DATE NOT NULL,
            pct_out DOUBLE(8,4) NOT NULL,
            shares_val BIGINT NOT NULL,
            created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
            updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
            UNIQUE(symbol, holders, date_reported)
        )
    '''

    cursor.execute(sql)

    sql = '''
        CREATE TABLE IF NOT EXISTS mtlfd_holders (
            id INT AUTO_INCREMENT PRIMARY KEY,
            symbol VARCHAR(10) NOT NULL,
            holders VARCHAR(60) NOT NULL,
            shares BIGINT NOT NULL,
            date_reported DATE NOT NULL,
            pct_out DOUBLE(8,4) NOT NULL,
            shares_val BIGINT NOT NULL,
            created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
            updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
            UNIQUE(symbol, holders, date_reported)
        )
    '''

    cursor.execute(sql)

    # Stocks to fetch
    sql = "SELECT name FROM ticker ORDER BY id"
    cursor.execute(sql)

    tickers = []
    for (name,) in cursor:
        tickers.append(name)

    # Fetch the stock information from Yahoo Finance
    data = {}
    for ticker in tickers:
        data[ticker] = yf.Ticker(ticker)

    for ticker in tickers:
        major_holders = data[ticker].major_holders
        # print(major_holders)
        values = [ticker]
        for index, row in major_holders.iterrows():
            # print(str(index) + " | " + row[0])
            values.append(row[0])
        # print(values)
        sql = """
            INSERT IGNORE INTO major_holders (symbol, insider_hold_pct, inst_hold_pct, float_hold_pct, num_inst)
            VALUES (%s, %s, %s, %s, %s)
            """
        cursor.execute(sql, values)

    for ticker in tickers:
        inst_holders = data[ticker].institutional_holders
        values = [ticker]
        # print(inst_holders)
        for index, row in inst_holders.iterrows():
            sql = """
                INSERT INTO inst_holders (symbol, holders, shares, date_reported, pct_out, shares_val)
                VALUES (%s, %s, %s, %s, %s, %s)
                ON DUPLICATE KEY UPDATE 
                        shares = VALUES(shares), 
                        pct_out = VALUES(pct_out), 
                        shares_val = VALUES(shares_val)
                """
            # shares_val should be the only one that changes over time.
            # shares_val = number of shares as of date reported x current price
            val = (ticker, row[0], row[1], row[2], row[3], row[4])
            cursor.execute(sql, val)

    for ticker in tickers:
        mtlfd_holders = data[ticker].mutualfund_holders
        values = [ticker]
        # print(inst_holders)
        for index, row in mtlfd_holders.iterrows():
            sql = """
                INSERT INTO mtlfd_holders (symbol, holders, shares, date_reported, pct_out, shares_val)
                VALUES (%s, %s, %s, %s, %s, %s)
                ON DUPLICATE KEY UPDATE 
                        shares = VALUES(shares), 
                        pct_out = VALUES(pct_out), 
                        shares_val = VALUES(shares_val)
                """
            val = (ticker, row[0], row[1], row[2], row[3], row[4])
            cursor.execute(sql, val)

    # Commit changes to database and close connection
    mydb.commit()
    cursor.close()
    mydb.close()

    print("Finished running holders.py")


if __name__ == '__main__':
    main()
