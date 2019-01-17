package com.redant.cluster.service.register;

import com.redant.cluster.node.Node;

/**
 * 服务注册-应用级别
 * @author houyi.wh
 * @date 2017/11/21
 **/
public interface ServiceRegister {

    /**
     * 注册节点
     * @param node 节点
     */
    void register(Node node);
}
