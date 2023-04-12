package com.capstone.realtimebackend.service.impl;

import com.capstone.realtimebackend.bean.*;
import com.capstone.realtimebackend.mapper.StockMapper;
import com.capstone.realtimebackend.service.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StockServiceImpl implements StockService {

    @Autowired(required = false)
    private StockMapper stockMapper;
    public Info getStockInfo(String symbol) {
        Info info = stockMapper.findInfoBySymbol(symbol);
//        System.out.println("symbol=" + symbol);
//        System.out.println("result = " + info);
        return info;
    }

    public HistPriceDTO getStockHistPrices(String symbol) {
        List<HistPrice> histPrices = stockMapper.findHistPricesBySymbol(symbol);
//        Map<String, List<Object>> map = new HashMap<>();
//        map.put("datetime", histPrices.stream().map(HistPrice::getDatetime).collect(Collectors.toList()));
//        map.put("closePrice", histPrices.stream().map(HistPrice::getClosePrice).collect(Collectors.toList()));
        HistPriceDTO histPriceDTO = new HistPriceDTO();
        histPriceDTO.setDatetimeList(histPrices.stream().map(HistPrice::getDatetime).collect(Collectors.toList()));
        histPriceDTO.setClosePriceList(histPrices.stream().map(HistPrice::getClosePrice).collect(Collectors.toList()));

//        System.out.println("symbol=" + symbol);
//        System.out.println("result = " + histPriceDTO);
        return histPriceDTO;
    }

    public HistMeta getStockHistMeta(String symbol) {
        HistMeta histMeta = stockMapper.findHistMetaBySymbol(symbol);
        return histMeta;
    }

    public MajorHoldersDTO getStockMajorHolders(String symbol) {
        MajorHoldersDTO majorHoldersDTO = new MajorHoldersDTO();
        majorHoldersDTO.setMajorHolders(stockMapper.findMajorHoldersBySymbol(symbol));
        majorHoldersDTO.setInstHolders(stockMapper.findInstHolderBySymbol(symbol));
        majorHoldersDTO.setMtlfdHolders(stockMapper.findMtlfdHolderBySymbol(symbol));
        return majorHoldersDTO;
    }

    public ShareCount getStockShareCount(String symbol) {
        ShareCount shareCount = stockMapper.findShareCountBySymbol(symbol);
        return shareCount;
    }

    public List<Earnings> getStockEarnings(String symbol) {
        List<Earnings> earningsList = stockMapper.findEarningsBySymbol(symbol);
        return earningsList;
    }

    public List<News> getStockNews(String symbol) {
        List<News> newsList = stockMapper.findNewsBySymbol(symbol);
        return newsList;
    }

    public List<Actions> getStockActions(String symbol) {
        List<Actions> actionsList = stockMapper.findActionsBySymbol(symbol);
        return actionsList;
    }

    public Map<String, Object> getWebStockInfo(String symbol) {
        Map<String, Object> webInfo = new HashMap<>();
        HistMeta histMeta = stockMapper.findHistMetaBySymbol(symbol);
        Info info = stockMapper.findInfoBySymbol(symbol);

        List<Earnings> earningsList = stockMapper.findEarningsBySymbol(symbol);
        webInfo.put("fiftyDayAver", info == null ? null : info.getFiftyDayAverage());
        webInfo.put("tenDayAverVol", info == null ? null : info.getTenDayAverVol());
        webInfo.put("threeMonthAverVol", info == null ? null : info.getThreeMonthAverVol());
        webInfo.put("twoHundredDayAver", info == null ? null : info.getTwoHundredDayAver());
        webInfo.put("yearHigh", info == null ? null : info.getYearHigh());
        webInfo.put("yearLow", info == null ? null : info.getYearLow());
        webInfo.put("yearChange", info == null ? null : info.getYearChange());
        webInfo.put("earningsDate", earningsList.size() == 0 ? null : earningsList.get(0).getEarningsDate());
        webInfo.put("reportedEps", earningsList.size() == 0 ? null : earningsList.get(0).getReportedEps());
        webInfo.put("surprisePct", earningsList.size() == 0 ? null : earningsList.get(0).getSurprisePct());
        webInfo.put("exchangeName", histMeta == null ? null : histMeta.getExchangeName());
        webInfo.put("currency", histMeta == null ? null : histMeta.getCurrency());
        webInfo.put("instrumentType", histMeta == null ? null : histMeta.getInstrumentType());
        webInfo.put("currRegMarketStart", histMeta == null ? null : histMeta.getCurrRegMarketStart());
        webInfo.put("currRegMarketEnd", histMeta == null ? null : histMeta.getCurrRegMarketEnd());
        return webInfo;
    }

    @PostConstruct
    public void init() {
        System.out.println("StockMapper: " + stockMapper); // Check whether StockMapper is injected correctly.
    }
}
