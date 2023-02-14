package com.atguigu.springboot.springbootdemo.service.impl;

import com.atguigu.springboot.springbootdemo.bean.Customer;
import com.atguigu.springboot.springbootdemo.mapper.CustomerMapper;
import com.atguigu.springboot.springbootdemo.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CustomerServiceImplNew implements CustomerService {

    @Autowired
    CustomerMapper customerMapper ;

    @Override
    public String doLogin(String username, String password) {

        System.out.println("CustomerServiceImplNew : 复杂的业务处理");
        //数据非空校验
        //数据格式的校验
        //。。。。。。
        // 调用数据层，比对数据库中的数据是否一致
        Customer customer = customerMapper.searchByUsernameAndPassword(username, password);
        if(customer != null ){
            return "ok";
        }else{
            return "error";
        }
    }
}
