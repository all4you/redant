package com.redant.cluster.zk;

import com.redant.common.util.PropertiesUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 针对Zk的全局配置项
 * @author gris.wang
 * @since 2017/11/21
 */
public class ZkConfig {

    private static final String ZK_CONFIG_PATH = "/zkConfig.properties";

    private ZkConfig(){

    }

    /**
     * 是否启用集群
     */
    private boolean useCluster = PropertiesUtil.getInstance(ZK_CONFIG_PATH).getBoolean("zk.use.cluster");

    /**
     * 单例
     */
    private static ZkConfig config;

    static{
        if(config==null){
            config = new ZkConfig();
        }
    }


    //======================================


    /**
     * 获取实例
     * @return
     */
    public static ZkConfig instance(){
        return config;
    }


    public ZkConfig useCluster(boolean useCluster){
        config.useCluster = useCluster;
        return config;
    }

    public boolean useCluster(){
        return config.useCluster;
    }


    @Override
    public String toString() {
        return "["+super.toString()+"]:{useCluster:"+config.useCluster+"}";
    }

    public static void main(String[] args) {
        int loopTimes = 200;

        class Runner implements Runnable{

            private Logger logger = LoggerFactory.getLogger(Runner.class);

            @Override
            public void run() {
                ZkConfig result = ZkConfig.instance();
                logger.info("result={},currentThread={}",result,Thread.currentThread().getName());
            }
        }

        for(int i=0;i<loopTimes;i++){
            new Thread(new Runner()).start();
        }

    }

}
