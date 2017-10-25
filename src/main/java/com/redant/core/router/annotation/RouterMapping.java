package com.redant.core.router.annotation;

import com.redant.common.enums.RequestMethod;
import com.redant.core.render.RenderType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RouterMapping {

    /**
     * 请求方法类型
     * @return
     */
    RequestMethod requestMethod() default RequestMethod.GET;

    /**
     * 请求的uri
     * @return
     */
    String path() default "";

    /**
     * 返回类型
     * @return
     */
    RenderType renderType() default RenderType.JSON;

}