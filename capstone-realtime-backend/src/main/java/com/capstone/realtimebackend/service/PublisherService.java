package com.capstone.realtimebackend.service;

import java.util.List;
import java.util.Map;

public interface PublisherService {
    List<Map<String, Object>> doPostRealtime(String td);

    Map<String, Object> doDetailByItem(String date, String keyWord, Integer pageNo, Integer pageSize);
}
