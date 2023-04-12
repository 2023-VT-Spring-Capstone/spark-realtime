package com.capstone.realtimebackend.mapper;

import com.capstone.realtimebackend.bean.*;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface StockMapper {
    public Info findInfoBySymbol(String symbol);
    public List<HistPrice> findHistPricesBySymbol(String symbol);
    public HistMeta findHistMetaBySymbol(String symbol);
    public MajorHolders findMajorHoldersBySymbol(String symbol);
    public List<InstHolder> findInstHolderBySymbol(String symbol);
    public List<MtlfdHolder> findMtlfdHolderBySymbol(String symbol);
    public ShareCount findShareCountBySymbol(String symbol);
    public List<Earnings> findEarningsBySymbol(String symbol);
    public List<News> findNewsBySymbol(String symbol);
    public List<Actions> findActionsBySymbol(String symbol);
}
