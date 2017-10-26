package com.redant.mybatissist.annotation;


import com.redant.mybatissist.enums.NameStyle;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Table {

    /**
     * 对应数据库表名
     * 若不指定则通过类名根据命名规则进行转换
     * @return
     */
    String name() default "";

    /**
     * sql执行中表名对应的别名
     * 不能为空
     * @return
     */
    String alias();

    /**
     * 命名规则
     * @return
     */
    NameStyle style() default NameStyle.CAMEL_HUMP;

}
