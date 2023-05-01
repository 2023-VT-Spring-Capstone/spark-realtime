package com.capstone.realtimebackend.bean;

import java.time.LocalDateTime;
import java.util.List;

public class HistPriceDTO {
    private List<LocalDateTime> datetimeList;
    private List<Double> closePriceList;
    private List<Double> openPriceList;
    private List<Double> highPriceList;
    private List<Double> lowPriceList;
    private List<Long> volumeList;

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

    public List<Double> getOpenPriceList() {
        return openPriceList;
    }

    public void setOpenPriceList(List<Double> openPriceList) {
        this.openPriceList = openPriceList;
    }

    public List<Double> getHighPriceList() {
        return highPriceList;
    }

    public void setHighPriceList(List<Double> highPriceList) {
        this.highPriceList = highPriceList;
    }

    public List<Double> getLowPriceList() {
        return lowPriceList;
    }

    public void setLowPriceList(List<Double> lowPriceList) {
        this.lowPriceList = lowPriceList;
    }

    public List<Long> getVolumeList() {
        return volumeList;
    }

    public void setVolumeList(List<Long> volumeList) {
        this.volumeList = volumeList;
    }
}
