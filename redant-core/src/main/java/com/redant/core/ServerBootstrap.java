package com.redant.core;

import com.redant.core.server.NettyServer;
import com.redant.core.server.Server;

/**
 * 服务端启动入口
 * @author gris.wang
 * @date 2017-10-20
 */
public final class ServerBootstrap {

    public static void main(String[] args) {
        Server nettyServer = new NettyServer();
        // 各种初始化工作
        nettyServer.preStart();
        // 启动服务器
        nettyServer.start();
    }

}
