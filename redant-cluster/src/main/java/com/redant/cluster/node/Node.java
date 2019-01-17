package com.redant.cluster.node;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.crypto.SecureUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.redant.core.common.util.GenericsUtil;

/**
 * Node
 * @author houyi.wh
 * @date 2017/11/20
 **/
public class Node {

    private static final String DEFAULT_HOST = GenericsUtil.getLocalIpV4();

    private static final int DEFAULT_PORT = 8088;

    public static final Node DEFAULT_PORT_NODE = new Node(DEFAULT_HOST,DEFAULT_PORT);

    private String id;

    private String host;

    private int port;

    public Node(int port){
        this(DEFAULT_HOST,port);
    }

    public Node(String host, int port){
        this(SecureUtil.md5(host+"&"+port),host,port);
    }

    public Node(String id, String host, int port){
        this.id = id;
        this.host = host;
        this.port = port;
    }

    public static Node getNodeWithArgs(String[] args){
        Node node = Node.DEFAULT_PORT_NODE;
        if(args.length>1 && NumberUtil.isInteger(args[1])){
            node = new Node(Integer.parseInt(args[1]));
        }
        return node;
    }

    /**
     * 从JsonObject中解析出SlaveNode
     * @param object 对象
     * @return 节点
     */
    public static Node parse(JSONObject object){
        if(object==null){
            return null;
        }
        String host = object.getString("host");
        int port = object.getIntValue("port");
        String id = object.getString("id");
        return new Node(id,host,port);
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
