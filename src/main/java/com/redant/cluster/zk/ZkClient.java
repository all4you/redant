package com.redant.cluster.zk;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryNTimes;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 操作ZK的客户端
 * @author gris.wang
 * @since 2017/11/21
 **/
public class ZkClient {

    private static final int DEFAULT_SESSION_TIMEOUT_MS = 60000;

    private static final int DEFAULT_CONNECTION_TIMEOUT_MS = 15000;

    /**
     * 操作ZK的客户端
     */
    private static Map<String,CuratorFramework> clients;

    private static Lock getLock;

    static{
        clients = new ConcurrentHashMap<String,CuratorFramework>();
        getLock = new ReentrantLock();
    }

    /**
     * 获取ZK客户端
     * @param zkServerAddress
     * @return
     */
    public static CuratorFramework getClient(String zkServerAddress){
        getLock.lock();
        CuratorFramework client;
        try {
            if(zkServerAddress == null || zkServerAddress.trim().length() == 0){
                return null;
            }
            client = clients.get(zkServerAddress);
            if(client==null){
                client = CuratorFrameworkFactory.newClient(
                        zkServerAddress,
                        DEFAULT_SESSION_TIMEOUT_MS,
                        DEFAULT_CONNECTION_TIMEOUT_MS,
                        new RetryNTimes(10, 5000)
                );
                client.start();
                clients.put(zkServerAddress,client);
            }
            return client;
        }finally {
            getLock.unlock();
        }
    }

}
