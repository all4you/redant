package com.redant.cluster.service.discover;

import com.redant.cluster.slave.SlaveNode;

/**
 * 服务发现
 * @author gris.wang
 * @since 2017/11/20
 **/
public class Discovery {

    private static ServiceDiscovery discovery;

    private Discovery(){}

    static{
        discovery = new DefaultServiceDiscovery();
    }

    public static SlaveNode nextSlave(){
        return discovery.discover();
    }

}
