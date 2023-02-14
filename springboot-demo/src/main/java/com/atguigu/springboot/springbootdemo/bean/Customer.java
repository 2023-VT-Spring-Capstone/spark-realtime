package com.atguigu.springboot.springbootdemo.bean;

import lombok.*;

//需要在IDEA的插件市场中搜索LomBok插件并安装
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class Customer {

    private String username;
    private String password;
    private String address ;
    private Integer age ;

    //构造器(无参 + 有参)
    //get/set
    //tostring
    //....
}

