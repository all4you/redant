package com.redant.cluster.zk;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xiaoleilu.hutool.io.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Map;
import java.util.Properties;


/**
 * ZK启动入口，可以启动单机模式或者（伪）集群模式
 * @author gris.wang
 * @since 2017/11/21
 **/
public class ZkBootstrap {

    private static final Logger logger = LoggerFactory.getLogger(ZkBootstrap.class);

    private static final String DEFAULT_ZK_STANDALONE_CFG = ZkBootstrap.class.getResource("/zk.cfg").getPath();

    private static final String DEFAULT_ZK_CLUSTER_CFG = ZkBootstrap.class.getResource("/zk_cluster.cfg").getPath();


    public static void main(String[] args) {
        // 启动ZK
        if(ZkConfig.instance().useCluster()) {
            ZkBootstrap.startCluster(null);
        }else{
            ZkBootstrap.startStandalone(null);
        }
    }


    /**
     * 解析出配置项
     * @param server
     * @return
     */
    private static Properties parse(JSONObject server){
        Properties properties = new Properties();
        if(server==null){
            logger.warn("parse error with server is null");
            return properties;
        }
        for(Map.Entry<String, Object> entry : server.entrySet()){
            String key = entry.getKey();
            String val = entry.getValue().toString();
            properties.put(key, val);
            if("dataDir".equals(key)){
                File dataDir = new File(val);
                if(!dataDir.exists()) {
                    FileUtil.mkdir(dataDir);
                }
            }
            if("dataLogDir".equals(key)){
                File dataLogDir = new File(val);
                if(!dataLogDir.exists()) {
                    FileUtil.mkdir(dataLogDir);
                }
            }
            if("myid".equals(key)){
                String[] strs = val.split(":");
                if(strs.length==2) {
                    String myid = strs[0];
                    String myidStr = strs[1];
                    File myidFile = new File(myid);
                    if (!(myidFile.exists())) {
                        FileUtil.touch(myidFile);
                        FileUtil.writeUtf8String(myidStr,myidFile);
                    }
                }
            }
        }
        return properties;
    }

    /**
     * 根据zk.cfg配置文件启动ZK单机
     * @param zkConfig
     */
    public static void startStandalone(String zkConfig) throws RuntimeException{
        try{
            if(zkConfig==null || zkConfig.trim().length()==0){
                zkConfig = DEFAULT_ZK_STANDALONE_CFG;
            }
            String configStr = FileUtil.readUtf8String(new File(zkConfig));
            JSONObject server = JSON.parseObject(configStr);
            Properties properties = parse(server);
            ZkServer zkServer = new ZkServer();
            zkServer.startStandalone(properties);
        }catch (Exception e) {
            logger.error("startStandalone error,cause:",e);
            throw new RuntimeException(e.getMessage());
        }
    }


    /**
     * 根据zk_cluster.cfg配置文件启动ZK集群
     * @param zkClusterConfig
     */
    public static void startCluster(String zkClusterConfig) throws RuntimeException{
        try{
            if(zkClusterConfig==null || zkClusterConfig.trim().length()==0){
                zkClusterConfig = DEFAULT_ZK_CLUSTER_CFG;
            }
            String configStr = FileUtil.readUtf8String(new File(zkClusterConfig));
            JSONObject obj = JSON.parseObject(configStr);
            JSONArray servers = obj.getJSONArray("servers");
            for(int i=0,size=servers.size();i<size;i++){
                JSONObject server = servers.getJSONObject(i);
                Properties properties = parse(server);
                ZkServer zkServer = new ZkServer();
                zkServer.startCluster(properties);
            }
        }catch (Exception e) {
            logger.error("startCluster error,cause:",e);
            throw new RuntimeException(e.getMessage());
        }
    }


}
