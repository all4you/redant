package com.redant.cluster.slave;

import com.redant.core.ServerInitUtil;

/**
 * SlaveServerBootstrap
 * @author gris.wang
 * @since 2017/11/20
 **/
public class SlaveServerBootstrap {

    public static void main(String[] args) {

        // 注册Slave到ZK

        // 各种初始化工作
        ServerInitUtil.init();

        // 启动SlaveServer
        SlaveServer slaveServer = new SlaveServer();
        slaveServer.start(SlaveNode.DEFAULT_PORT_NODE);

    }
}
