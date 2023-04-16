package com.capstone.realtimebackend.mapper;

import java.util.List;
import java.util.Map;

public interface PublisherMapper {

    List<Map<String, Object>> searchPost(String td);

    Map<String, Object> searchDetailByItem(String date, String keyWord, int from, Integer pageSize);
}
