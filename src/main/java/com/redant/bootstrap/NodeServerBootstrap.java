package com.redant.bootstrap;

import com.redant.cluster.node.Node;
import com.redant.cluster.node.NodeServer;
import com.redant.cluster.service.register.RegisteryWrapper;
import com.redant.core.ServerInitUtil;
import com.redant.zk.ZkServer;
import com.xiaoleilu.hutool.util.NumberUtil;
import com.xiaoleilu.hutool.util.StrUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * NodeServerBootstrap
 * @author gris.wang
 * @since 2017/11/20
 **/
public class NodeServerBootstrap {

    private static final Logger LOGGER = LoggerFactory.getLogger(NodeServerBootstrap.class);

    public static void main(String[] args) {

        String zkServerAddress = ZkServer.getZkServerAddress();
        if(args.length>0 && StrUtil.isNotBlank(args[0])){
            LOGGER.info("zkServerAddress is read from args");
            zkServerAddress = args[0];
        }
        if(StrUtil.isBlank(zkServerAddress)){
            LOGGER.error("zkServerAddress is blank please check file={}",ZkServer.ZOOKEEPER_ADDRESS_CFG);
            System.exit(1);
        }

        Node node = Node.DEFAULT_PORT_NODE;
        if(args.length>0){
            zkServerAddress = args[0];
            if(args.length>1 && NumberUtil.isInteger(args[1])){
                node = new Node(Integer.parseInt(args[1]));
            }
        }

        // 注册Slave到ZK
        RegisteryWrapper.register(zkServerAddress, node);

        // 各种初始化工作
        ServerInitUtil.init();

        // 启动SlaveServer
        NodeServer nodeServer = new NodeServer();
        nodeServer.start(node);

    }
}
