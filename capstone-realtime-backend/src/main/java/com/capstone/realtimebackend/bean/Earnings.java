package com.capstone.realtimebackend.bean;

import java.time.LocalDateTime;

public class Earnings {
    private int id;
    private String symbol;
    private LocalDateTime earningsDate;
    private Float epsEstimate;
    private Float reportedEps;
    private Double surprisePct;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public int getId() {
        return id;
    }

    public String getSymbol() {
        return symbol;
    }

    public LocalDateTime getEarningsDate() {
        return earningsDate;
    }

    public Float getEpsEstimate() {
        return epsEstimate;
    }

    public Float getReportedEps() {
        return reportedEps;
    }

    public Double getSurprisePct() {
        return surprisePct;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
