package com.mybatissist.provider;

import com.mybatissist.enums.QueryModel;
import com.mybatissist.enums.QueryStyle;

import java.io.Serializable;

/**
 * 列名-属性对
 * @author gris.wang
 * @create 2017-10-20
 */
public class ColumnProp implements Serializable{

    /**
     * 查询方式
     * AND、OR
     */
    private QueryStyle queryStyle;

    /**
     * 查询模式
     * EQUAL、LIKE
     */
    private QueryModel queryModel;

    /**
     * 列名
     */
    private String column;

    /**
     * 属性
     */
    private String prop;


    public ColumnProp(QueryStyle queryStyle, QueryModel queryModel, String column, String prop){
        this.queryStyle = queryStyle;
        this.queryModel = queryModel;
        this.column = column;
        this.prop = prop;
    }

    public QueryStyle getQueryStyle() {
        return queryStyle;
    }

    public void setQueryStyle(QueryStyle queryStyle) {
        this.queryStyle = queryStyle;
    }

    public QueryModel getQueryModel() {
        return queryModel;
    }

    public void setQueryModel(QueryModel queryModel) {
        this.queryModel = queryModel;
    }

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public String getProp() {
        return prop;
    }

    public void setProp(String prop) {
        this.prop = prop;
    }
}
