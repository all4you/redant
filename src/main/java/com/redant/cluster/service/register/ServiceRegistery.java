package com.redant.cluster.service.register;

import com.redant.cluster.node.Node;

/**
 * @author gris.wang
 * @since 2017/11/21
 **/
public interface ServiceRegistery {

    /**
     * 注册节点
     * @param node
     */
    void register(Node node);
}
