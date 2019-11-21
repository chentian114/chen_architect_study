package com.chen.java8.common;

import com.alibaba.fastjson.JSONObject;

import java.io.Serializable;

/**
 * 分页请求消息对象
 * @author ChenTian
 */
public class RequestMsg implements Serializable {
    public static final int DEFAULT_PAGE = 0;
    public static final int DEFAULT_SIZE = 10;

    /**
     *  查询页数，从0开始
     */
    private int currPage = DEFAULT_PAGE;

    /**
     * 每页最大数量
     */
    private int pageSize = DEFAULT_SIZE;


    /** 查询请求参数{"key1":"value1","key2":"value2"} */
     private JSONObject params;

    public RequestMsg() {
    }

    public RequestMsg(int currPage,int pageSize) {
        this.currPage = currPage;
        this.pageSize = pageSize;
    }

    public RequestMsg(PageInfo pageInfo) {
        if(pageInfo==null){
            return;
        }
        if(pageInfo.getPageNumber() >= 0) {
            this.currPage = pageInfo.getPageNumber();
        }
        if(pageInfo.getPageSize() >0 ) {
            this.pageSize = pageInfo.getPageSize();
        }
    }

    public RequestMsg(Object params, PageInfo pageInfo) {
        this(pageInfo);
        if(params==null){
            return;
        }
        this.params = (JSONObject) JSONObject.toJSON(params);
    }

    public int getCurrPage() {
        return currPage;
    }

    public void setCurrPage(int currPage) {
        this.currPage = currPage;
    }

    public int getPageSize() {
        if(pageSize==0){
            return DEFAULT_SIZE;
        }
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public JSONObject getParams() {
        return params;
    }

    public void setParams(JSONObject params) {
        this.params = params;
    }
}
