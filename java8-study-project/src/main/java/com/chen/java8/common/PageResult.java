package com.chen.java8.common;

import java.util.List;

/**
 * 分页查询返回结果对象
 * @author ChenTian
 * @date 2019/11/4
 */
public class PageResult<T> {
    /** 分页查询结果列表 */
    private List<T> content;
    /** 分页请求信息 */
    private PageInfo pageable;
    /** 元素总数量 */
    private long totalElements;
    /** 总页数*/
    private long totalPages;

    public List<T> getContent() {
        return content;
    }

    public void setContent(List<T> content) {
        this.content = content;
    }

    public PageInfo getPageable() {
        return pageable;
    }

    public void setPageable(PageInfo pageable) {
        this.pageable = pageable;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }

    public long getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }
}
