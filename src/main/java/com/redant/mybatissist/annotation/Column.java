package com.redant.mybatissist.annotation;


import com.redant.mybatissist.enums.QueryModel;
import com.redant.mybatissist.enums.QueryStyle;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Column {

    /**
     * 字段名
     * 不填默认取属性名
     * @return
     */
    String name() default "";

    /**
     * 查询方式
     * 默认以AND方式
     * @return
     */
    QueryStyle queryStyle() default QueryStyle.AND;

    /**
     * 查询模式
     * 默认以EQUAL方式
     * @return
     */
    QueryModel queryModel() default QueryModel.EQUAL;

    /**
     * 是否忽略该字段
     * 为true时则select和where中都不使用该字段
     * @return
     */
    boolean ignore() default false;

}
