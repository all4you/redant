package com.redant.cluster.zk;


import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.NetUtil;

import java.io.File;
import java.util.List;

/**
 * 启动zk所需要的配置信息
 * @author houyi.wh
 * @date 2017/11/21
 */
public class ZkConfig {

    public static final String STANDALONE_MODEL = "standalone";

    public static final String CLUSTER_MODEL = "cluster";

    private String model;

    private List<Config> configs;

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public List<Config> getConfigs() {
        return configs;
    }

    public void setConfigs(List<Config> configs) {
        this.configs = configs;
    }

    /**
     * 将zk服务的地址写入文件
     * @return
     */
    public String writeZkAddressToFile(){
        String host = NetUtil.getLocalhostStr();
        if(CollectionUtil.isNotEmpty(configs)){
            int s = CLUSTER_MODEL.equalsIgnoreCase(model)?configs.size():1;
            String[] addressArray = new String[s];
            for(int i=0;i<s;i++){
                addressArray[i] = host+":"+configs.get(i).getClientPort();
            }
            String address = ArrayUtil.join(addressArray,",");
            // 将zk服务的地址写入文件
            File addressCfg = new File(ZkServer.ZOOKEEPER_ADDRESS_CFG);
            if(!addressCfg.getParentFile().exists()){
                FileUtil.mkdir(addressCfg.getParentFile());
            }
            if (!addressCfg.exists()) {
                FileUtil.touch(addressCfg);
            }
            FileUtil.writeUtf8String(address,addressCfg);
            return address;
        }
        return null;
    }

    public static class Config{
        /**
         * 必填项
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

        private int maxClientCnxns = 60;

        /**
         * 集群模式下需要，格式为：
         * /home/zookeeper/1/data/myid:1
         * /home/zookeeper/2/data/myid:2
         * /home/zookeeper/3/data/myid:3
         */
        private String myid;

        /**
         * 集群模式下需要
         * "127.0.0.1:2887:3887",
         * "127.0.0.1:2888:3888",
         * "127.0.0.1:2889:3889"
         */
        private List<String> servers;

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

        public String getMyid() {
            return myid;
        }

        public void setMyid(String myid) {
            this.myid = myid;
        }

        public List<String> getServers() {
            return servers;
        }

        public void setServers(List<String> servers) {
            this.servers = servers;
        }
    }


}
