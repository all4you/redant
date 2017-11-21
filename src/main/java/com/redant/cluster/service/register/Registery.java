package com.redant.cluster.service.register;

import com.redant.cluster.slave.SlaveNode;
import com.redant.cluster.zk.ZkServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 服务注册
 * @author gris.wang
 * @since 2017/11/20
 **/
public class Registery {

    private static final Logger logger = LoggerFactory.getLogger(Registery.class);

    private static ServiceRegistery registery;

    private Registery(){}

    static{
        registery = new DefaultServiceRegistery(ZkServer.ZK_SERVER_ADDRESS);
    }

    public static void register(SlaveNode slaveNode){
        if(registery!=null){
            registery.register(slaveNode);
        }else{
            logger.error("registery is null");
        }
    }

}
