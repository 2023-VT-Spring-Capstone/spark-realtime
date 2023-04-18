package com.capstone.realtimebackend.service.impl;

import com.capstone.realtimebackend.mapper.PublisherMapper;
import com.capstone.realtimebackend.service.PublisherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

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
    public Map<String, Object> doDetailByItem(String date, String keyWord, Integer pageNo, Integer pageSize) {
        int from = (pageNo - 1) * pageSize;
        Map<String, Object> searchResults = publisherMapper.searchDetailByItem(date, keyWord, from, pageSize);
        return searchResults;
    }
}
