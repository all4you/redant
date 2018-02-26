package com.redant.bootstrap;

import com.redant.cluster.proxy.ProxyServer;
import com.redant.cluster.service.discover.DiscoveryWrapper;
import com.redant.zk.ZkServer;
import com.xiaoleilu.hutool.util.StrUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ProxyServerBootstrap
 * @author gris.wang
 * @since 2017/11/20
 **/
public class ProxyServerBootstrap {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProxyServerBootstrap.class);

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

        // 监听SlaveNode的变化
        DiscoveryWrapper.watchSlave(zkServerAddress);

        // 启动MasterServer
        ProxyServer proxyServer = new ProxyServer();
        proxyServer.start();

    }
}
