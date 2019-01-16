package com.redant.core.common.util;

/**
 * 线程工具类
 * @author houyi.wh
 * @date 2017-10-20
 */
public class ThreadUtil {

    /**
     * 获取当前线程名称
     * @return 线程名称
     */
    public static String currentThreadName(){
        return Thread.currentThread().getName();
    }


}
