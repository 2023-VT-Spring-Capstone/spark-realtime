package com.capstone.realtimebackend.bean;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Actions {
    private int id;
    private String symbol;
    private LocalDate actionDate;
    private double dividends;
    private double stockSplits;
    private LocalDateTime createdAt;

    public int getId() {
        return id;
    }

    public String getSymbol() {
        return symbol;
    }

    public LocalDate getActionDate() {
        return actionDate;
    }

    public double getDividends() {
        return dividends;
    }

    public double getStockSplits() {
        return stockSplits;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
