import yfinance as yf
import mysql.connector
from db_config import db_config


def main():

    print("Run info.py")

    # Connect to MySQL database
    mydb = mysql.connector.connect(**db_config)

    # Create a cursor object
    cursor = mydb.cursor()

    # Define the SQL query to create the stocks table
    sql = '''
        CREATE TABLE IF NOT EXISTS info (
          symbol VARCHAR(10) NOT NULL UNIQUE,
          currency VARCHAR(10) NOT NULL,
          day_high DOUBLE(12,4) NOT NULL,
          day_low DOUBLE(12,4) NOT NULL,
          exchange VARCHAR(15) NOT NULL,
          fifty_day_average DOUBLE(12,4),
          last_price DOUBLE(12,4),
          last_volume BIGINT NOT NULL,
          market_cap BIGINT NOT NULL,
          day_open DOUBLE(12,4) NOT NULL,
          pre_close DOUBLE(12,4) NOT NULL,
          quote_type VARCHAR(20) NOT NULL,
          reg_pre_close DOUBLE(12,4) NOT NULL,
          num_shares BIGINT NOT NULL,
          ten_day_aver_vol BIGINT NOT NULL,
          three_month_aver_vol BIGINT NOT NULL,
          timezone VARCHAR(50) NOT NULL,
          two_hundred_day_aver DOUBLE NOT NULL,
          year_change DOUBLE NOT NULL,
          year_high DOUBLE(12,4) NOT NULL,
          year_low DOUBLE(12,4) NOT NULL,
          created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
          updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
        )
    '''

    # Execute the SQL query
    cursor.execute(sql)

    keys = ['currency', 'dayHigh', 'dayLow', 'exchange', 'fiftyDayAverage', 'lastPrice', 'lastVolume',
               'marketCap', 'open', 'previousClose', 'quoteType', 'regularMarketPreviousClose', 'shares', 'tenDayAverageVolume',
               'threeMonthAverageVolume', 'timezone', 'twoHundredDayAverage', 'yearChange', 'yearHigh', 'yearLow']

    # Stock to fetch
    sql = "SELECT name FROM ticker ORDER BY id"
    cursor.execute(sql)

    tickers = []
    for (name,) in cursor:
        tickers.append(name)

    for ticker in tickers:
        stock = yf.Ticker(ticker)
        fast_info = stock.fast_info
        info_dict = {}
        for key in fast_info:
            value = fast_info[key]

            # 如果是market_cap欄位，去除小數點
            if key == 'marketCap':
                value = int(value)
            # 如果是其他欄位，四捨五入到小數點後4位
            elif isinstance(value, float):
                value = round(value, 4)
            info_dict[key] = value
        # 獲取值，如果鍵不存在，則設置為空字符串
        values = [info_dict.get(key, None) for key in keys]
        values.insert(0, ticker)
        # 定義INSERT語句

        sql = """
            INSERT INTO info
            (symbol, currency, day_high, day_low, exchange, fifty_day_average, last_price, last_volume, market_cap, day_open,
             pre_close, quote_type, reg_pre_close, num_shares, ten_day_aver_vol, three_month_aver_vol, timezone,
             two_hundred_day_aver, year_change, year_high, year_low)
            VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s)
            ON DUPLICATE KEY UPDATE 
                        currency = VALUES(currency), 
                        day_high = VALUES(day_high), 
                        day_low = VALUES(day_low),
                        exchange = VALUES(exchange),
                        fifty_day_average = VALUES(fifty_day_average), 
                        last_price = VALUES(last_price), 
                        last_volume = VALUES(last_volume),
                        market_cap = VALUES(market_cap), 
                        day_open = VALUES(day_open), 
                        pre_close = VALUES(pre_close),
                        quote_type = VALUES(quote_type), 
                        reg_pre_close = VALUES(reg_pre_close), 
                        num_shares = VALUES(num_shares),
                        ten_day_aver_vol = VALUES(ten_day_aver_vol), 
                        three_month_aver_vol = VALUES(three_month_aver_vol), 
                        timezone = VALUES(timezone),
                        two_hundred_day_aver = VALUES(two_hundred_day_aver), 
                        year_change = VALUES(year_change), 
                        year_high = VALUES(year_high),
                        year_low = VALUES(year_low)
            """
        cursor.execute(sql, values)

    # Commit changes to database and close connection
    mydb.commit()
    cursor.close()
    mydb.close()

    print("Finished running info.py")


if __name__ == '__main__':
    main()
