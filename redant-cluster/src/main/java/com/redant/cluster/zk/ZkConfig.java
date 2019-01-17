package com.redant.cluster.zk;


import com.redant.core.common.util.GenericsUtil;

import java.util.Properties;

/**
 * 启动zk所需要的配置信息
 * @author houyi.wh
 * @date 2017/11/21
 */
public class ZkConfig {

    private interface ZkConstant {
        int CLIENT_PORT = 2181;
        String DATA_DIR = "/Users/houyi/zookeeper/data";
        String DATA_LOG_DIR = "/Users/houyi/zookeeper/log";
    }

    /**
     * 客户端连接的端口
     */
    private int clientPort;

    private int tickTime = 2000;

    private int initLimit = 10;

    private int syncLimit = 5;

    /**
     * 数据存储目录，格式为：
     * /home/zookeeper/1/data
     */
    private String dataDir;

    /**
     * 日志存储目录，格式为：
     * /home/zookeeper/1/log
     */
    private String dataLogDir;

    /**
     * 客户端连接数上限
     */
    private int maxClientCnxns = 60;

    public ZkConfig(int clientPort,String dataDir,String dataLogDir){
        this.clientPort = clientPort;
        this.dataDir = dataDir;
        this.dataLogDir = dataLogDir;
    }

    public static ZkConfig DEFAULT = new ZkConfig(ZkConstant.CLIENT_PORT,ZkConstant.DATA_DIR,ZkConstant.DATA_LOG_DIR);


    public String generateZkAddress(){
        return GenericsUtil.getLocalIpV4()+":"+this.clientPort;
    }

    public Properties toProp(){
        Properties properties = new Properties();
        properties.put("clientPort",this.clientPort);
        properties.put("clientPortAddress",GenericsUtil.getLocalIpV4());
        properties.put("tickTime",this.tickTime);
        properties.put("initLimit",this.initLimit);
        properties.put("syncLimit",this.syncLimit);
        properties.put("dataDir",this.dataDir);
        properties.put("dataLogDir",this.dataLogDir);
        properties.put("maxClientCnxns",this.maxClientCnxns);

        return properties;
    }





    public int getClientPort() {
        return clientPort;
    }

    public void setClientPort(int clientPort) {
        this.clientPort = clientPort;
    }

    public int getTickTime() {
        return tickTime;
    }

    public void setTickTime(int tickTime) {
        this.tickTime = tickTime;
    }

    public int getInitLimit() {
        return initLimit;
    }

    public void setInitLimit(int initLimit) {
        this.initLimit = initLimit;
    }

    public int getSyncLimit() {
        return syncLimit;
    }

    public void setSyncLimit(int syncLimit) {
        this.syncLimit = syncLimit;
    }

    public String getDataDir() {
        return dataDir;
    }

    public void setDataDir(String dataDir) {
        this.dataDir = dataDir;
    }

    public String getDataLogDir() {
        return dataLogDir;
    }

    public void setDataLogDir(String dataLogDir) {
        this.dataLogDir = dataLogDir;
    }

    public int getMaxClientCnxns() {
        return maxClientCnxns;
    }

    public void setMaxClientCnxns(int maxClientCnxns) {
        this.maxClientCnxns = maxClientCnxns;
    }


}
