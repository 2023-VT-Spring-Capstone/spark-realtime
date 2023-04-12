package com.capstone.realtimebackend.bean;

import java.time.LocalDateTime;

public class MajorHolders {
    private Integer id;
    private String symbol;
    private String insiderHoldPct;
    private String instHoldPct;
    private String floatHoldPct;
    private Integer numInst;
    private LocalDateTime createdAt;

    public Integer getId() {
        return id;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getInsiderHoldPct() {
        return insiderHoldPct;
    }

    public String getInstHoldPct() {
        return instHoldPct;
    }

    public String getFloatHoldPct() {
        return floatHoldPct;
    }

    public Integer getNumInst() {
        return numInst;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
