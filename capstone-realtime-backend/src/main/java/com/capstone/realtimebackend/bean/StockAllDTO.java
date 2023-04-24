package com.capstone.realtimebackend.bean;

import java.util.List;

public class StockAllDTO {
    private Info info;
    private HistPriceDTO histPrice;
    private HistMeta histMeta;
    private MajorHolders majorHolders;
    private List<InstHolder> instHolders;
    private List<MtlfdHolder> mtlfdHolders;
    private ShareCount shareCount;
    private List<Earnings> earningsList;
    private List<News> newsList;

    public Info getInfo() {
        return info;
    }

    public void setInfo(Info info) {
        this.info = info;
    }

    public HistPriceDTO getHistPrice() {
        return histPrice;
    }

    public void setHistPrice(HistPriceDTO histPrice) {
        this.histPrice = histPrice;
    }

    public HistMeta getHistMeta() {
        return histMeta;
    }

    public void setHistMeta(HistMeta histMeta) {
        this.histMeta = histMeta;
    }

    public MajorHolders getMajorHolders() {
        return majorHolders;
    }

    public void setMajorHolders(MajorHolders majorHolders) {
        this.majorHolders = majorHolders;
    }

    public List<InstHolder> getInstHolders() {
        return instHolders;
    }

    public void setInstHolders(List<InstHolder> instHolders) {
        this.instHolders = instHolders;
    }

    public List<MtlfdHolder> getMtlfdHolders() {
        return mtlfdHolders;
    }

    public void setMtlfdHolders(List<MtlfdHolder> mtlfdHolders) {
        this.mtlfdHolders = mtlfdHolders;
    }

    public ShareCount getShareCount() {
        return shareCount;
    }

    public void setShareCount(ShareCount shareCount) {
        this.shareCount = shareCount;
    }

    public List<Earnings> getEarningsList() {
        return earningsList;
    }

    public void setEarningsList(List<Earnings> earningsList) {
        this.earningsList = earningsList;
    }

    public List<News> getNewsList() {
        return newsList;
    }

    public void setNewsList(List<News> newsList) {
        this.newsList = newsList;
    }
}
