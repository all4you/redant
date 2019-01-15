package com.redant.cluster.service.register;

import com.redant.cluster.slave.Node;

/**
 * @author houyi.wh
 * @date 2017/11/21
 **/
public interface ServiceRegistery {

    /**
     * 注册节点
     * @param node 节点
     */
    void register(Node node);
}
