package com.capstone.realtimebackend.bean;

import java.time.LocalDateTime;

public class HistPrice {
    private Integer id;
    private String symbol;
    private LocalDateTime datetime;
    private Double openPrice;
    private Double highPrice;
    private Double lowPrice;
    private Double closePrice;
    private Long volume;
    private String timeRange;
    private String dataGranularity;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Integer getId() {
        return id;
    }

    public String getSymbol() {
        return symbol;
    }

    public LocalDateTime getDatetime() {
        return datetime;
    }

    public Double getOpenPrice() {
        return openPrice;
    }

    public Double getHighPrice() {
        return highPrice;
    }

    public Double getLowPrice() {
        return lowPrice;
    }

    public Double getClosePrice() {
        return closePrice;
    }

    public Long getVolume() {
        return volume;
    }

    public String getTimeRange() {
        return timeRange;
    }

    public String getDataGranularity() {
        return dataGranularity;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
