package com.redant.cluster.slave;

import com.redant.common.constants.CommonConstants;
import com.xiaoleilu.hutool.util.NetUtil;

/**
 * SlaveNode
 * @author gris.wang
 * @since 2017/11/20
 **/
public class SlaveNode {

    private static final String DEFAULT_HOST = NetUtil.getLocalhostStr();

    public static final SlaveNode DEFAULT_PORT_NODE = new SlaveNode(DEFAULT_HOST,CommonConstants.SERVER_PORT);

    private String host;

    private int port;

    public SlaveNode(int port){
        this(DEFAULT_HOST,port);
    }

    public SlaveNode(String host,int port){
        this.host = host;
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public String toString() {
        return "{host:"+host+",port:"+port+"}";
    }

}
