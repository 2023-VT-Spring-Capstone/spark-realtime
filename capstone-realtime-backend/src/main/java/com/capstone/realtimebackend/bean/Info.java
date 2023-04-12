package com.capstone.realtimebackend.bean;

import java.time.LocalDateTime;

public class Info {
    private String symbol;
    private String currency;
    private Double dayHigh;
    private Double dayLow;
    private String exchange;
    private Double fiftyDayAverage;
    private Double lastPrice;
    private Long lastVolume;
    private Long marketCap;
    private Double dayOpen;
    private Double preClose;
    private String quoteType;
    private Double regPreClose;
    private Long numShares;
    private Long tenDayAverVol;
    private Long threeMonthAverVol;
    private String timezone;
    private Double twoHundredDayAver;
    private Double yearChange;
    private Double yearHigh;
    private Double yearLow;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Double getDayHigh() {
        return dayHigh;
    }

    public void setDayHigh(Double dayHigh) {
        this.dayHigh = dayHigh;
    }

    public Double getDayLow() {
        return dayLow;
    }

    public void setDayLow(Double dayLow) {
        this.dayLow = dayLow;
    }

    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    public Double getFiftyDayAverage() {
        return fiftyDayAverage;
    }

    public void setFiftyDayAverage(Double fiftyDayAverage) {
        this.fiftyDayAverage = fiftyDayAverage;
    }

    public Double getLastPrice() {
        return lastPrice;
    }

    public void setLastPrice(Double lastPrice) {
        this.lastPrice = lastPrice;
    }

    public Long getLastVolume() {
        return lastVolume;
    }

    public void setLastVolume(Long lastVolume) {
        this.lastVolume = lastVolume;
    }

    public Long getMarketCap() {
        return marketCap;
    }

    public void setMarketCap(Long marketCap) {
        this.marketCap = marketCap;
    }

    public Double getDayOpen() {
        return dayOpen;
    }

    public void setDayOpen(Double dayOpen) {
        this.dayOpen = dayOpen;
    }

    public Double getPreClose() {
        return preClose;
    }

    public void setPreClose(Double preClose) {
        this.preClose = preClose;
    }

    public String getQuoteType() {
        return quoteType;
    }

    public void setQuoteType(String quoteType) {
        this.quoteType = quoteType;
    }

    public Double getRegPreClose() {
        return regPreClose;
    }

    public void setRegPreClose(Double regPreClose) {
        this.regPreClose = regPreClose;
    }

    public Long getNumShares() {
        return numShares;
    }

    public void setNumShares(Long numShares) {
        this.numShares = numShares;
    }

    public Long getTenDayAverVol() {
        return tenDayAverVol;
    }

    public void setTenDayAverVol(Long tenDayAverVol) {
        this.tenDayAverVol = tenDayAverVol;
    }

    public Long getThreeMonthAverVol() {
        return threeMonthAverVol;
    }

    public void setThreeMonthAverVol(Long threeMonthAverVol) {
        this.threeMonthAverVol = threeMonthAverVol;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public Double getTwoHundredDayAver() {
        return twoHundredDayAver;
    }

    public void setTwoHundredDayAver(Double twoHundredDayAver) {
        this.twoHundredDayAver = twoHundredDayAver;
    }

    public Double getYearChange() {
        return yearChange;
    }

    public void setYearChange(Double yearChange) {
        this.yearChange = yearChange;
    }

    public Double getYearHigh() {
        return yearHigh;
    }

    public void setYearHigh(Double yearHigh) {
        this.yearHigh = yearHigh;
    }

    public Double getYearLow() {
        return yearLow;
    }

    public void setYearLow(Double yearLow) {
        this.yearLow = yearLow;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}

