package com.capstone.realtimebackend.bean;

import java.time.LocalDateTime;
import java.util.List;

public class HistPriceDTO {
    private List<LocalDateTime> datetimeList;
    private List<Double> closePriceList;

    public void setDatetimeList(List<LocalDateTime> datetimeList) {
        this.datetimeList = datetimeList;
    }

    public void setClosePriceList(List<Double> closePriceList) {
        this.closePriceList = closePriceList;
    }

    public List<LocalDateTime> getDatetimeList() {
        return datetimeList;
    }

    public List<Double> getClosePriceList() {
        return closePriceList;
    }
}
