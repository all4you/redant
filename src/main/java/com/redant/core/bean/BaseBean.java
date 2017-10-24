package com.redant.core.bean;


import com.alibaba.fastjson.JSON;

import java.io.Serializable;


/**
 * BaseBean 所有bean都继承该类
 * @author gris.wang
 * @create 2017-10-20
 */
public class BaseBean implements Serializable {
    private static final long serialVersionUID = -4976516540408695147L;

    public BaseBean() {
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}