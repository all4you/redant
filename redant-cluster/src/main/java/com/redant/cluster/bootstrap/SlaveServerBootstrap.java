package com.redant.cluster.bootstrap;

import com.redant.cluster.slave.Node;
import com.redant.cluster.slave.SlaveServer;
import com.redant.cluster.zk.ZkServer;
import com.redant.core.server.Server;

/**
 * SlaveServerBootstrap
 * @author houyi.wh
 * @date 2017/11/20
 **/
public class SlaveServerBootstrap {

    public static void main(String[] args) {
        String zkServerAddress = ZkServer.getZkServerAddressWithArgs(args);
        Node node = Node.getNodeWithArgs(args);

        // 启动SlaveServer
        Server nodeServer = new SlaveServer(zkServerAddress,node);
        nodeServer.preStart();
        nodeServer.start();
    }

}
