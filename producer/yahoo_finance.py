import yfinance as yf
import mysql.connector

# Connect to MySQL database
mydb = mysql.connector.connect(
    host="localhost",
    port=3307,
    user="local",
    password="local",
    database="yfinance"
)

# Create a cursor object
cursor = mydb.cursor()

# Define the SQL query to create the stocks table
sql = 'CREATE TABLE stocks (' \
      'id INT AUTO_INCREMENT PRIMARY KEY, ' \
      'symbol VARCHAR(10) NOT NULL, ' \
      'date DATE NOT NULL, ' \
      'open FLOAT NOT NULL, ' \
      'high FLOAT NOT NULL, ' \
      'low FLOAT NOT NULL, ' \
      'close FLOAT NOT NULL, ' \
      'volume BIGINT NOT NULL)'

# Execute the SQL query
cursor.execute(sql)


# Define the stocks to fetch
stocks = ['GME', 'TSLA', 'AMC']

# Loop through each stock
for stock in stocks:
    # Fetch the stock information from Yahoo Finance
    data = yf.download(stock, start="2021-01-01", end="2023-03-05")

    # Loop through each row of data and insert it into the MySQL database
    for index, row in data.iterrows():
        sql = "INSERT INTO stocks (symbol, date, open, high, low, close, volume) VALUES (%s, %s, %s, %s, %s, %s, %s)"
        val = (stock, index.date(), row['Open'], row['High'], row['Low'], row['Close'], row['Volume'])
        cursor.execute(sql, val)

# Commit changes to database and close connection
mydb.commit()
cursor.close()
mydb.close()
