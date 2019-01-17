package com.redant.cluster.service.discover;

import com.redant.cluster.node.Node;

/**
 * 服务发现-应用级别
 * @author houyi.wh
 * @date 2017/11/20
 **/
public interface ServiceDiscover {

    /**
     * 监听Slave节点
     */
    void watch();

    /**
     * 发现可用的Slave节点
     * @return 可用节点
     */
    Node discover();

}
