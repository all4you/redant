package com.redant.core.anno;

import java.lang.annotation.*;

/**
 * 排序规则，升序排序
 * @author houyi.wh
 * @date 2019-01-14
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
public @interface Order {

    /**
     * 最低优先级
     */
    int LOWEST_PRECEDENCE = Integer.MAX_VALUE;
    /**
     * 最高优先级
     */
    int HIGHEST_PRECEDENCE = Integer.MIN_VALUE;

    /**
     * The order value. Lowest precedence by default.
     *
     * @return the order value
     */
    int value() default LOWEST_PRECEDENCE;
}
