package com.redant.cluster.service.register;

import com.redant.cluster.slave.SlaveNode;

/**
 * @author gris.wang
 * @since 2017/11/21
 **/
public interface ServiceRegistery {

    /**
     * 注册节点
     * @param slaveNode
     */
    void register(SlaveNode slaveNode);
}
