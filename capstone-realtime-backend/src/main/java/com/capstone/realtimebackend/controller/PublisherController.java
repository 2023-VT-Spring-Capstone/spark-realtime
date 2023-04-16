package com.capstone.realtimebackend.controller;
import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import com.capstone.realtimebackend.mapper.PublisherMapper;
import com.capstone.realtimebackend.service.PublisherService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PublisherController {
    @Autowired
    PublisherService publisherService;
    @GetMapping("stock/{td}")
    public List<Map<String, Object>> doPostRealtime(@PathVariable("td") String td) {
        List<Map<String, Object>> postResults = publisherService.doPostRealtime(td);
        return postResults;
    }

    @GetMapping("detailByItem")
    public Map<String, Object> detailByItem(@RequestParam("date") String date ,
                                            @RequestParam("keyWord") String keyWord ,
                                            @RequestParam(value ="pageNo" , required = false  , defaultValue = "1") Integer pageNo ,
                                            @RequestParam(value = "pageSize" , required = false , defaultValue = "20") Integer pageSize){
        Map<String, Object> results =  publisherService.doDetailByItem(date, keyWord, pageNo, pageSize);
        return results ;
    }
}
