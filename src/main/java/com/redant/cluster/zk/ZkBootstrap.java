package com.redant.cluster.zk;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redant.common.util.PropertiesUtil;
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

    private static final String DEFAULT_ZK_STANDALONE_CFG = ZkBootstrap.class.getResource("/zk_standalone.cfg").getPath();

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
     * 启动之前检查并创建相关目录和文件
     * @param properties
     * @return
     */
    private static void preCheck(Properties properties){
        if(properties==null){
            logger.warn("parse error with properties is null");
            return;
        }
        for(Map.Entry<Object, Object> entry : properties.entrySet()){
            String key = entry.getKey().toString();
            String val = entry.getValue().toString();
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
    }

    /**
     * 根据zk.cfg配置文件启动ZK单机
     * @param zkCfg
     */
    public static void startStandalone(String zkCfg) throws RuntimeException{
        try{
            if(zkCfg==null || zkCfg.trim().length()==0){
                zkCfg = DEFAULT_ZK_STANDALONE_CFG;
            }
            String configStr = FileUtil.readUtf8String(new File(zkCfg));
            JSONObject server = JSON.parseObject(configStr);
            String zkPropertiesPath = server.getString("zkPropertiesPath");
            if(zkPropertiesPath==null || zkPropertiesPath.trim().length()==0){
                throw new RuntimeException("Please provide zkPropertiesPath");
            }
            preCheck(PropertiesUtil.getPropertiesByResource(zkPropertiesPath));
            ZkServer zkServer = new ZkServer();
            // 获取properties的真实路径
            String realPropertiesPath = ZkBootstrap.class.getResource(zkPropertiesPath).getPath();
            zkServer.startStandalone(realPropertiesPath);
        }catch (Exception e) {
            logger.error("startStandalone error,cause:",e);
            throw new RuntimeException(e.getMessage());
        }
    }


    /**
     * 根据zk_cluster.cfg配置文件启动ZK集群
     * @param zkClusterCfg
     */
    public static void startCluster(String zkClusterCfg) throws RuntimeException{
        try{
            if(zkClusterCfg==null || zkClusterCfg.trim().length()==0){
                zkClusterCfg = DEFAULT_ZK_CLUSTER_CFG;
            }
            String configStr = FileUtil.readUtf8String(new File(zkClusterCfg));
            JSONObject obj = JSON.parseObject(configStr);
            JSONArray servers = obj.getJSONArray("servers");
            for(int i=0,size=servers.size();i<size;i++){
                JSONObject server = servers.getJSONObject(i);
                String zkPropertiesPath = server.getString("zkPropertiesPath");
                if(zkPropertiesPath==null || zkPropertiesPath.trim().length()==0){
                    throw new RuntimeException("Please provide zkPropertiesPath");
                }
                preCheck(PropertiesUtil.getPropertiesByResource(zkPropertiesPath));
                ZkServer zkServer = new ZkServer();
                // 获取properties的真实路径
                String realPropertiesPath = ZkBootstrap.class.getResource(zkPropertiesPath).getPath();
                zkServer.startCluster(realPropertiesPath);
            }
        }catch (Exception e) {
            logger.error("startCluster error,cause:",e);
            throw new RuntimeException(e.getMessage());
        }
    }


}
