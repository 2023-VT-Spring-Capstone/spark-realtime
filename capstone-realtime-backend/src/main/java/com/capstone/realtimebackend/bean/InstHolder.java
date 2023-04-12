package com.capstone.realtimebackend.bean;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class InstHolder {
    private Integer id;
    private String symbol;
    private String holders;
    private Long shares;
    private LocalDate dateReported;
    private Double pctOut;
    private Long sharesVal;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Integer getId() {
        return id;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getHolders() {
        return holders;
    }

    public Long getShares() {
        return shares;
    }

    public LocalDate getDateReported() {
        return dateReported;
    }

    public Double getPctOut() {
        return pctOut;
    }

    public Long getSharesVal() {
        return sharesVal;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}



