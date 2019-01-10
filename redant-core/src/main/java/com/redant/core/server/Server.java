package com.redant.core.server;

/**
 * @author houyi.wh
 * @date 2019-01-10
 */
public interface Server {

    /**
     * 启动服务器之前的事件处理
     */
    void preStart();

    /**
     * 启动服务器
     */
    void start();

}