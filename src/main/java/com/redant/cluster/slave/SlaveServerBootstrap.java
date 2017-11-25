package com.redant.cluster.slave;

import com.redant.cluster.service.register.Registery;
import com.redant.core.ServerInitUtil;
import com.xiaoleilu.hutool.util.NumberUtil;

/**
 * SlaveServerBootstrap
 * @author gris.wang
 * @since 2017/11/20
 **/
public class SlaveServerBootstrap {

    public static void main(String[] args) {

        SlaveNode slaveNode;
        if(args.length==1 && NumberUtil.isInteger(args[0])){
            slaveNode = new SlaveNode(Integer.parseInt(args[0]));
        }else{
            slaveNode = SlaveNode.DEFAULT_PORT_NODE;
        }

        // 注册Slave到ZK
        Registery.register(slaveNode);

        // 各种初始化工作
        ServerInitUtil.init();

        // 启动SlaveServer
        SlaveServer slaveServer = new SlaveServer();
        slaveServer.start(slaveNode);

    }
}
