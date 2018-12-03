package com.jean.redis.client.model;

/**
 * 分页参数
 *
 * @author jinshubao
 * @date 2018/12/03
 */
public class Pageable {

    public Pageable() {
    }

    public Pageable(String cursor, int count, String pattern) {
        this.cursor = cursor;
        this.count = count;
        this.pattern = pattern;
    }

    private String cursor;

    private int count;

    private String pattern;


    public String getCursor() {
        return cursor;
    }

    public void setCursor(String cursor) {
        this.cursor = cursor;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }
}
