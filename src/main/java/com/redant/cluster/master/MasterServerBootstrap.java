package com.redant.cluster.master;

import com.redant.cluster.service.discover.Discovery;

/**
 * MasterServerBootstrap
 * @author gris.wang
 * @since 2017/11/20
 **/
public class MasterServerBootstrap {

    public static void main(String[] args) {

        // 监听SlaveNode的变化
        Discovery.watchSlave();

        // 启动MasterServer
        MasterServer masterServer = new MasterServer();
        masterServer.start();

    }
}
