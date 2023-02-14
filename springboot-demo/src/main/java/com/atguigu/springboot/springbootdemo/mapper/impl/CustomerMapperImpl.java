package com.atguigu.springboot.springbootdemo.mapper.impl;

import com.atguigu.springboot.springbootdemo.bean.Customer;
import com.atguigu.springboot.springbootdemo.mapper.CustomerMapper;
import org.springframework.stereotype.Repository;

/**
 * 数据层组件
 */
@Repository  //标识成数据层组件(Spring)
public class CustomerMapperImpl  implements CustomerMapper {

    @Override
    public Customer searchByUsernameAndPassword(String username, String password) {

        System.out.println( "CustomerMapperImpl:  数据库的查询操作.... ");
        // JDBC
        // MyBatis
        // Hibernate
        // .....
        return new Customer(username, password , null ,null );

        //return null ;
    }
}
