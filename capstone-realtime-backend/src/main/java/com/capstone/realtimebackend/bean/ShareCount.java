package com.capstone.realtimebackend.bean;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class ShareCount {
    private int id;
    private String symbol;
    private LocalDate recordDate;
    private long shareCount;
    private LocalDateTime createdAt;

    public int getId() {
        return id;
    }

    public String getSymbol() {
        return symbol;
    }

    public LocalDate getRecordDate() {
        return recordDate;
    }

    public long getShareCount() {
        return shareCount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
