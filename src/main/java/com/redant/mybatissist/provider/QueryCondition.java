package com.redant.mybatissist.provider;

import com.redant.mybatissist.enums.QueryModel;
import com.redant.mybatissist.enums.QueryStyle;

import java.io.Serializable;

/**
 * 查询条件
 * @author gris.wang
 * @create 2017-10-20
 */
public class QueryCondition implements Serializable{

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
     * 表字段
     */
    private String column;

    /**
     * 属性
     */
    private String prop;


    public QueryCondition(QueryStyle queryStyle,QueryModel queryModel,String column,String prop){
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
