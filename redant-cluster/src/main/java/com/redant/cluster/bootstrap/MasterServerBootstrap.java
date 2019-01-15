package com.redant.cluster.bootstrap;

import com.redant.cluster.master.MasterServer;
import com.redant.cluster.zk.ZkServer;
import com.redant.core.server.Server;

/**
 * MasterServerBootstrap
 * @author houyi.wh
 * @date 2017/11/20
 **/
public class MasterServerBootstrap {

    public static void main(String[] args) {
        String zkServerAddress = ZkServer.getZkServerAddressWithArgs(args);

        // 启动MasterServer
        Server proxyServer = new MasterServer(zkServerAddress);
        proxyServer.preStart();
        proxyServer.start();
    }

}
