package com.redant.cluster.bootstrap;

import com.redant.cluster.node.Node;
import com.redant.cluster.slave.SlaveServer;
import com.redant.cluster.zk.ZkConfig;
import com.redant.cluster.zk.ZkServer;
import com.redant.core.server.Server;

/**
 * SlaveServerBootstrap
 * @author houyi.wh
 * @date 2017/11/20
 **/
public class SlaveServerBootstrap {

    public static void main(String[] args) {
        String zkAddress = ZkServer.getZkAddressArgs(args,ZkConfig.DEFAULT);
        Node node = Node.getNodeWithArgs(args);

        // 启动SlaveServer
        Server slaveServer = new SlaveServer(zkAddress,node);
        slaveServer.preStart();
        slaveServer.start();
    }

}
