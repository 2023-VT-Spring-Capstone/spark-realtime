package com.atguigu.springboot.springbootdemo.mapper;

import com.atguigu.springboot.springbootdemo.bean.Customer;

/**
 * 数据层接口
 *
 * 目前，数据层一般都是基于MyBatis实现的。MyBatis的玩法是只写接口+SQL即可，不需要自己写实现类.
 */
public interface CustomerMapper {

    Customer searchByUsernameAndPassword(String username, String password) ;
}
