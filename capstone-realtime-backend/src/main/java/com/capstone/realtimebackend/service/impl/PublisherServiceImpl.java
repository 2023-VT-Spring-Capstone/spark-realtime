package com.capstone.realtimebackend.service.impl;

import com.capstone.realtimebackend.mapper.PublisherMapper;
import com.capstone.realtimebackend.service.PublisherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class PublisherServiceImpl implements PublisherService {
    @Autowired
    PublisherMapper publisherMapper;
    public List<Map<String, Object>> doPostRealtime(String td) {
        List<Map<String, Object>> postResults = publisherMapper.searchPost(td);
        return postResults;
    }


    @Override
    public Map<String, Object> doDetailByItem(String startDate, String endDate, String keyWord, Integer pageNo, Integer pageSize) {
        int from = (pageNo - 1) * pageSize;
        Map<String, Object> searchResults = publisherMapper.searchDetailByItem(startDate, endDate, keyWord, from, pageSize);
        return searchResults;
    }
}
