package com.capstone.demo.capstonerealtimedemo.controller;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;


// controller
@RestController  // @controller + @ responsebody
public class CustomerController {

    /**
     *
     * @return
     */
    @RequestMapping("helloworld") // mapping the client's request
//    @ResponseBody // return the value as a string(json) to the client
    public String helloWorld() {

     return "success";

    }

}
