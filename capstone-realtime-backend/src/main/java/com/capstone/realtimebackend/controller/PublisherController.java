package com.capstone.realtimebackend.controller;
import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import com.capstone.realtimebackend.mapper.PublisherMapper;
import com.capstone.realtimebackend.service.PublisherService;
import org.springframework.web.bind.annotation.*;

@RestController
public class PublisherController {
    @Autowired
    PublisherService publisherService;

    //http://localhost/detailByItem?startDate=2023-04-15&endDate=2023-04-18&keyWord=bank&pageNo=1&pageSize=20
    @CrossOrigin(origins = "*")
    @GetMapping("detailByItem")
    public Map<String, Object> detailByItem(@RequestParam(value = "startDate" , required = false) String startDate ,
                                            @RequestParam(value = "endDate" , required = false) String endDate ,
                                            @RequestParam(value = "keyWord" , required = false, defaultValue = "stock") String keyWord ,
                                            @RequestParam(value ="pageNo" , required = false  , defaultValue = "1") Integer pageNo ,
                                            @RequestParam(value = "pageSize" , required = false , defaultValue = "20") Integer pageSize){
        Map<String, Object> results =  publisherService.doDetailByItem(startDate, endDate, keyWord, pageNo, pageSize);
        return results ;
    }
}
