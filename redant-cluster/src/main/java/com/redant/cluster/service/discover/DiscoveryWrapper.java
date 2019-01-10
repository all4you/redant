package com.redant.cluster.service.discover;

import com.redant.cluster.slave.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 服务发现
 * @author gris.wang
 * @date 2017/11/20
 **/
public class DiscoveryWrapper {

    private static final Logger LOGGER = LoggerFactory.getLogger(DiscoveryWrapper.class);

    private static ServiceDiscovery discovery;

    private DiscoveryWrapper(){}


    /**
     * 监听SlaveNode的变化
     * @param zkServerAddress ZooKeeper服务端地址
     */
    public static void watchSlave(String zkServerAddress){
        if(discovery==null) {
            discovery = new DefaultServiceDiscovery(zkServerAddress);
        }
        discovery.watchSlave();
    }

    /**
     * 获取下一个SlaveNode节点
     */
    public static Node nextSlave(){
        if(discovery!=null) {
            return discovery.discover();
        }else{
            LOGGER.error("discovery is null");
            return null;
        }
    }

}
