package com.jean.redis.client.model;

import java.util.Collection;

/**
 * 分页对象
 *
 * @author jinshubao
 * @date 2018/12/03
 */
public class Page<T> {

    private String cursor;
    private int size;
    private long total;

    private Collection<T> contents;


    public Page() {
    }

    public Page(String cursor, int size, long total, Collection<T> contents) {
        this.cursor = cursor;
        this.size = size;
        this.total = total;
        this.contents = contents;
    }

    public long getTotal() {
        return total;
    }

    public long getSize() {
        return size;
    }

    public String getCursor() {
        return cursor;
    }

    public Collection<T> getContents() {
        return contents;
    }
}
