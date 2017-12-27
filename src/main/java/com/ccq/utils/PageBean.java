package com.ccq.utils;

import java.util.List;

/**
 * @author ccq
 * @Description
 * @date 2017/12/3 17:07
 */
public class PageBean<T> {
    private List<T> list;
    private long total;

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }
}
