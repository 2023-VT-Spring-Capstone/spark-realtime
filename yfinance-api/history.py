import yfinance as yf
import mysql.connector
from db_config import db_config


def main():

    print("Run history.py")

    # Connect to MySQL database
    mydb = mysql.connector.connect(**db_config)

    # Create a cursor object
    cursor = mydb.cursor()

    sql = '''
        CREATE TABLE IF NOT EXISTS hist_price (
            id INT AUTO_INCREMENT PRIMARY KEY,
            symbol VARCHAR(10) NOT NULL,
            datetime DATETIME NOT NULL,
            open_price DOUBLE(12,4) NOT NULL,
            high_price DOUBLE(12,4) NOT NULL,
            low_price DOUBLE(12,4) NOT NULL,
            close_price DOUBLE(12,4) NOT NULL,
            volume BIGINT NOT NULL,
            time_range VARCHAR(5) NOT NULL,
            data_granularity VARCHAR(5) NOT NULL,
            created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
            updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
            UNIQUE (symbol, datetime, time_range, data_granularity)
        )
    '''

    cursor.execute(sql)

    sql = '''
        CREATE TABLE IF NOT EXISTS hist_meta (
        id INT AUTO_INCREMENT PRIMARY KEY,
        currency VARCHAR(6) NOT NULL,
        symbol VARCHAR(10) NOT NULL,
        exchange_name VARCHAR(8) NOT NULL,
        instrument_type VARCHAR(15) NOT NULL,
        first_trade_date DATETIME NOT NULL,
        regular_market_time DATETIME NOT NULL,
        gmt_offset INT NOT NULL,
        timezone VARCHAR(6) NOT NULL,
        exchange_tz_name VARCHAR(30) NOT NULL,
        reg_market_price DOUBLE(12,4) NOT NULL,
        chart_prev_close DOUBLE(12,4) NOT NULL,
        price_hint TINYINT NOT NULL,
        data_granularity VARCHAR(4) NOT NULL,
        time_range VARCHAR(4) NOT NULL,
        curr_pre_market_start DATETIME NOT NULL,
        curr_pre_market_end DATETIME NOT NULL,
        curr_reg_market_start DATETIME NOT NULL,
        curr_reg_market_end DATETIME NOT NULL,
        curr_post_market_start DATETIME NOT NULL,
        curr_post_market_end DATETIME NOT NULL,
        created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        UNIQUE (symbol, time_range)
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

    _API_NAME = 'history'

    cursor.execute("INSERT IGNORE INTO params (api_name, param_name, param_value) VALUES ('{}', 'period', '1y')".format(_API_NAME))
    cursor.execute("INSERT IGNORE INTO params (api_name, param_name, param_value) VALUES ('{}', 'interval', '1d')".format(_API_NAME))

    cursor.execute("SELECT param_value FROM params WHERE api_name='{}' AND param_name='period'".format(_API_NAME))
    period = cursor.fetchone()[0]

    cursor.execute("SELECT param_value FROM params WHERE api_name='{}' AND param_name='interval'".format(_API_NAME))
    interval = cursor.fetchone()[0]

    # print("Period:", period)
    # print("Interval:", interval)

    # Stocks to fetch
    sql = "SELECT name FROM ticker ORDER BY id"
    cursor.execute(sql)

    tickers = []
    for (name,) in cursor:
        tickers.append(name)

    # Loop through each stock
    for ticker in tickers:
        # Fetch the stock information from Yahoo Finance
        data = yf.Ticker(ticker)
        hist_price = data.history(interval=interval, period=period)
        hist_meta = data.history_metadata

        # Loop through each row of data and insert it into the MySQL database
        for index, row in hist_price.iterrows():
            # print(index.strftime('%Y-%m-%d %H:%M:%S'))
            sql = """
                    INSERT INTO hist_price (symbol, datetime, open_price, high_price, low_price, close_price, volume, 
                    time_range, data_granularity)
                    VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s)
                    ON DUPLICATE KEY UPDATE 
                        open_price = VALUES(open_price), 
                        high_price = VALUES(high_price), 
                        low_price = VALUES(low_price), 
                        close_price = VALUES(close_price), 
                        volume = VALUES(volume)
                """

            val = (
                ticker, index.strftime('%Y-%m-%d %H:%M:%S'), float(row['Open']), float(row['High']), float(row['Low']),
                float(row['Close']), int(row['Volume']), period, interval
            )

            cursor.execute(sql, val)
        mydb.commit()

        hist_meta_keys = ['currency', 'symbol', 'exchangeName', 'instrumentType', 'firstTradeDate', 'regularMarketTime',
                          'gmtoffset', 'timezone', 'exchangeTimezoneName', 'regularMarketPrice', 'chartPreviousClose',
                          'priceHint', 'currentTradingPeriod', 'dataGranularity', 'range', 'validRanges']
        # print(hist_meta)

        preStart = hist_meta['currentTradingPeriod']['pre']['start'].strftime('%Y-%m-%d')
        preEnd = hist_meta['currentTradingPeriod']['pre']['end'].strftime('%Y-%m-%d')
        regStart = hist_meta['currentTradingPeriod']['regular']['start'].strftime('%Y-%m-%d')
        regEnd = hist_meta['currentTradingPeriod']['regular']['end'].strftime('%Y-%m-%d')
        postStart = hist_meta['currentTradingPeriod']['post']['start'].strftime('%Y-%m-%d')
        postEnd = hist_meta['currentTradingPeriod']['post']['end'].strftime('%Y-%m-%d')
        hist_meta_keys.remove('currentTradingPeriod')
        hist_meta_keys.remove('validRanges')
        # values = [hist_meta.get(key, None) for key in hist_meta_keys]
        values = []
        for key in hist_meta_keys:
            value = None
            if key == 'regularMarketTime' or key == 'firstTradeDate':
                value = hist_meta.get(key, None).strftime('%Y-%m-%d')
            else:
                value = hist_meta.get(key, None)
            values.append(value)
        values.append(preStart)
        values.append(preEnd)
        values.append(regStart)
        values.append(regEnd)
        values.append(postStart)
        values.append(postEnd)
        # values.append(hist_meta.get('symbol'))
        # values.append(hist_meta.get('dataGranularity'))
        # values.append(hist_meta.get('range'))

        sql = '''
                INSERT INTO hist_meta (
                    currency, symbol, exchange_name, instrument_type, first_trade_date, regular_market_time, 
                    gmt_offset, timezone, exchange_tz_name, reg_market_price, chart_prev_close, price_hint, 
                    data_granularity, time_range, curr_pre_market_start, curr_pre_market_end, curr_reg_market_start, 
                    curr_reg_market_end, curr_post_market_start, curr_post_market_end
                )
                VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s)
                ON DUPLICATE KEY UPDATE
                    currency = VALUES(currency),
                    exchange_name = VALUES(exchange_name),
                    instrument_type = VALUES(instrument_type),
                    first_trade_date = VALUES(first_trade_date),
                    regular_market_time = VALUES(regular_market_time),
                    gmt_offset = VALUES(gmt_offset),
                    timezone = VALUES(timezone),
                    exchange_tz_name = VALUES(exchange_tz_name),
                    reg_market_price = VALUES(reg_market_price),
                    chart_prev_close = VALUES(chart_prev_close),
                    price_hint = VALUES(price_hint),
                    data_granularity = VALUES(data_granularity),
                    time_range = VALUES(time_range),
                    curr_pre_market_start = VALUES(curr_pre_market_start),
                    curr_pre_market_end = VALUES(curr_pre_market_end),
                    curr_reg_market_start = VALUES(curr_reg_market_start),
                    curr_reg_market_end = VALUES(curr_reg_market_end),
                    curr_post_market_start = VALUES(curr_post_market_start),
                    curr_post_market_end = VALUES(curr_post_market_end)
            '''

        cursor.execute(sql, values)
        mydb.commit()

    cursor.close()
    mydb.close()

    print("Finished running history.py")


if __name__ == '__main__':
    main()
