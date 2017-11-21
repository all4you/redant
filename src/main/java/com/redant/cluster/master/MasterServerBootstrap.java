package com.redant.cluster.master;

import com.redant.cluster.service.discover.Discovery;
import com.redant.cluster.zk.ZkBootstrap;
import com.redant.cluster.zk.ZkConfig;

/**
 * MasterServerBootstrap
 * @author gris.wang
 * @since 2017/11/20
 **/
public class MasterServerBootstrap {

    public static void main(String[] args) {

        // 启动ZK
        if(ZkConfig.instance().useCluster()) {
            ZkBootstrap.startCluster(null);
        }else{
            ZkBootstrap.startStandalone(null);
        }

        // 监听SlaveNode的变化
        Discovery.watchSlave();

        // 启动MasterServer
        MasterServer masterServer = new MasterServer();
        masterServer.start();

    }
}
