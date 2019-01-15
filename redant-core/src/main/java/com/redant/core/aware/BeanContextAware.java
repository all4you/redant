package com.redant.core.aware;

import com.redant.core.bean.context.BeanContext;

/**
 * @author houyi.wh
 * @date 2019-01-14
 */
public interface BeanContextAware extends Aware{

    /**
     * 设置BeanContext
     * @param beanContext BeanContext对象
     */
    void setBeanContext(BeanContext beanContext);
}