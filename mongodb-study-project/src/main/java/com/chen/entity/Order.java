package com.chen.entity;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.Date;

@Document(collection = "orders")
public class Order {
    @Id
    private String id;

    private String orderCode;

    private String userCode;

    private Date orderTime;

    private Integer price;

    private String[] Auditors;

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    public Date getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(Date orderTime) {
        this.orderTime = orderTime;
    }


    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String useCode) {
        this.userCode = useCode;
    }

    public String[] getAuditors() {
        return Auditors;
    }

    public void setAuditors(String[] auditors) {
        Auditors = auditors;
    }



}