package com.redant.cluster.service.discover;

import com.redant.cluster.slave.Node;

/**
 * @author houyi.wh
 * @date 2017/11/20
 **/
public interface ServiceDiscovery {

    /**
     * 监听Slave节点
     */
    void watchSlave();

    /**
     * 发现可用的Slave节点
     * @return 可用节点
     */
    Node discover();

}
