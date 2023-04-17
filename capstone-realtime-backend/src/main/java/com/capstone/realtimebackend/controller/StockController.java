package com.capstone.realtimebackend.controller;

import com.capstone.realtimebackend.bean.*;
import com.capstone.realtimebackend.service.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class StockController {

    @Autowired
    StockService stockService;

    /**
     * http://localhost:8080/stock/APPL/info
     */

    @CrossOrigin(origins = "*")
    @GetMapping("stock/{symbol}/info")
    public Map<String, Object> stock_info(@PathVariable("symbol") String symbol) {
//        return stockService.getStockInfo(symbol);
//        return stockService.getStockHistPrices(symbol);
//        return stockService.getStockHistMeta(symbol);
//        return stockService.getStockMajorHolders(symbol);
//        return stockService.getStockShareCount(symbol);
//        return stockService.getStockEarnings(symbol);
//        return stockService.getStockNews(symbol);
        return stockService.getWebStockInfo(symbol);
    }

    @CrossOrigin(origins = "*")
    @GetMapping("stock/{symbol}/chart")
    public HistPriceDTO stock_chart(@PathVariable("symbol") String symbol) {
        return stockService.getStockHistPrices(symbol);
    }
}