package com.redant.cluster.service.register;

import com.redant.cluster.node.Node;

/**
 * 服务注册
 * @author gris.wang
 * @since 2017/11/20
 **/
public class RegisteryWrapper {

    private static ServiceRegistery registery;

    private RegisteryWrapper(){}


    /**
     * 注册服务到ZooKeeper中去
     * @param zkServerAddress
     * @param node
     */
    public static void register(String zkServerAddress,Node node){
        if(registery==null){
            registery = new DefaultServiceRegistery(zkServerAddress);
        }
        registery.register(node);
    }

}
