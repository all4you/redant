package com.redant.cluster.bootstrap;

import com.redant.cluster.zk.ZkConfig;
import com.redant.cluster.zk.ZkServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * ZK启动入口
 * @author houyi.wh
 * @date 2017/11/21
 **/
public class ZkBootstrap {

    private static final Logger LOGGER = LoggerFactory.getLogger(ZkBootstrap.class);


    public static void main(String[] args) {
        try {
            ZkServer zkServer = new ZkServer();
            zkServer.startStandalone(ZkConfig.DEFAULT);
        }catch (Exception e){
            LOGGER.error("ZkBootstrap start failed,cause:",e);
            System.exit(1);
        }
    }


}
