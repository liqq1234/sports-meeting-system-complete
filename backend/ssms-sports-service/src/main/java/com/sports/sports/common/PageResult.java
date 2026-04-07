package com.sports.sports.common;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class PageResult<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    private long total;
    private long pageNum;
    private long pageSize;
    private long pages;
    private List<T> records;

    public PageResult() {}

    public PageResult(long total, long pageNum, long pageSize, long pages, List<T> records) {
        this.total = total;
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.pages = pages;
        this.records = records;
    }
}
