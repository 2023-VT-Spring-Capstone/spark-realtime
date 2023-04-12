package com.capstone.realtimebackend.service;

import com.capstone.realtimebackend.bean.*;

import java.util.List;
import java.util.Map;

public interface StockService {
    public Info getStockInfo(String symbol);
    public HistPriceDTO getStockHistPrices(String symbol);
    public HistMeta getStockHistMeta(String symbol);
    public MajorHoldersDTO getStockMajorHolders(String symbol);
    public ShareCount getStockShareCount(String symbol);
    public List<Earnings> getStockEarnings(String symbol);
    public List<News> getStockNews(String symbol);
    public List<Actions> getStockActions(String symbol);
    public Map<String, Object> getWebStockInfo(String symbol);
}
