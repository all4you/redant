package com.redant.common.util;

/**
 * 线程工具类
 * @author gris.wang
 * @create 2017-10-20
 */
public class ThreadUtil {

    /**
     * 获取当前线程名称
     * @return
     */
    public static String currentThreadName(){
        return Thread.currentThread().getName();
    }


}
