package com.capstone.realtimebackend.bean;

import java.time.LocalDateTime;

public class HistMeta {
    private int id;
    private String currency;
    private String symbol;
    private String exchangeName;
    private String instrumentType;
    private LocalDateTime firstTradeDate;
    private LocalDateTime regularMarketTime;
    private int gmtOffset;
    private String timezone;
    private String exchangeTzName;
    private double regMarketPrice;
    private double chartPrevClose;
    private int priceHint;
    private String dataGranularity;
    private String timeRange;
    private LocalDateTime currPreMarketStart;
    private LocalDateTime currPreMarketEnd;
    private LocalDateTime currRegMarketStart;
    private LocalDateTime currRegMarketEnd;
    private LocalDateTime currPostMarketStart;
    private LocalDateTime currPostMarketEnd;

    public int getId() {
        return id;
    }

    public String getCurrency() {
        return currency;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getExchangeName() {
        return exchangeName;
    }

    public String getInstrumentType() {
        return instrumentType;
    }

    public LocalDateTime getFirstTradeDate() {
        return firstTradeDate;
    }

    public LocalDateTime getRegularMarketTime() {
        return regularMarketTime;
    }

    public int getGmtOffset() {
        return gmtOffset;
    }

    public String getTimezone() {
        return timezone;
    }

    public String getExchangeTzName() {
        return exchangeTzName;
    }

    public double getRegMarketPrice() {
        return regMarketPrice;
    }

    public double getChartPrevClose() {
        return chartPrevClose;
    }

    public int getPriceHint() {
        return priceHint;
    }

    public String getDataGranularity() {
        return dataGranularity;
    }

    public String getTimeRange() {
        return timeRange;
    }

    public LocalDateTime getCurrPreMarketStart() {
        return currPreMarketStart;
    }

    public LocalDateTime getCurrPreMarketEnd() {
        return currPreMarketEnd;
    }

    public LocalDateTime getCurrRegMarketStart() {
        return currRegMarketStart;
    }

    public LocalDateTime getCurrRegMarketEnd() {
        return currRegMarketEnd;
    }

    public LocalDateTime getCurrPostMarketStart() {
        return currPostMarketStart;
    }

    public LocalDateTime getCurrPostMarketEnd() {
        return currPostMarketEnd;
    }
}