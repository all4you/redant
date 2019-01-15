package com.redant.core.bean.context;

/**
 * Bean上下文
 * @author houyi.wh
 * @date 2017-10-20
 */
public interface BeanContext {

    /**
     * 获得Bean
     * @param name Bean的名称
     * @return Bean
     */
    Object getBean(String name);

    /**
     * 获得Bean
     * @param name Bean的名称
     * @param clazz Bean的类
     * @param <T> 泛型
     * @return Bean
     */
    <T> T getBean(String name,Class<T> clazz);

}
