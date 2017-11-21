package com.redant.cluster.slave;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xiaoleilu.hutool.crypto.SecureUtil;
import com.xiaoleilu.hutool.util.NetUtil;

/**
 * SlaveNode
 * @author gris.wang
 * @since 2017/11/20
 **/
public class SlaveNode {

    private static final String DEFAULT_HOST = NetUtil.getLocalhostStr();

    private static final int DEFAULT_PORT = 8088;

    public static final SlaveNode DEFAULT_PORT_NODE = new SlaveNode(DEFAULT_HOST,DEFAULT_PORT);

    private String id;

    private String host;

    private int port;

    public SlaveNode(int port){
        this(DEFAULT_HOST,port);
    }

    public SlaveNode(String host,int port){
        this.id = SecureUtil.md5(host+"&"+port);
        this.host = host;
        this.port = port;
    }

    public SlaveNode(String id,String host,int port){
        this.id = id;
        this.host = host;
        this.port = port;
    }

    /**
     * 从JsonObject中解析出SlaveNode
     * @param object
     * @return
     */
    public static SlaveNode parse(JSONObject object){
        if(object==null){
            return null;
        }
        String host = object.getString("host");
        int port = object.getIntValue("port");
        String id = object.getString("id");
        return new SlaveNode(id,host,port);
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

    public String getId(){
        return id;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }

}
