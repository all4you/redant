package com.redant.main;

import com.redant.core.ServerInitUtil;
import com.redant.core.server.NettyServer;

/**
 * 服务端启动入口
 * @author gris.wang
 * @create 2017-10-20
 */
public final class ServerBootstrap {

    public static void main(String[] args) {

        // 各种初始化工作
        ServerInitUtil.init();

        NettyServer nettyServer = new NettyServer();
        nettyServer.start();
    }

}
