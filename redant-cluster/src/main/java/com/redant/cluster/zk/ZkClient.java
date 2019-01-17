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
 * @author houyi.wh
 * @date 2017/11/21
 **/
public class ZkClient {

    /**
     * 节点的session超时时间，当Slave服务停掉后，
     * curator客户端需要等待该节点超时后才会触发CHILD_REMOVED事件
     */
    private static final int DEFAULT_SESSION_TIMEOUT_MS = 5000;

    private static final int DEFAULT_CONNECTION_TIMEOUT_MS = 15000;

    /**
     * 操作ZK的客户端
     */
    private static Map<String,CuratorFramework> clients;

    private static Lock lock;

    static{
        clients = new ConcurrentHashMap<>();
        lock = new ReentrantLock();
    }

    /**
     * 获取ZK客户端
     * @param zkAddress zk服务端地址
     * @return zk客户端
     */
    public static CuratorFramework getClient(String zkAddress){
        if(zkAddress == null || zkAddress.trim().length() == 0){
            return null;
        }
        CuratorFramework client = clients.get(zkAddress);
        if(client==null){
            lock.lock();
            try {
                if(!clients.containsKey(zkAddress)) {
                    client = CuratorFrameworkFactory.newClient(
                            zkAddress,
                            DEFAULT_SESSION_TIMEOUT_MS,
                            DEFAULT_CONNECTION_TIMEOUT_MS,
                            new RetryNTimes(10, 5000)
                    );
                    client.start();
                    clients.putIfAbsent(zkAddress,client);
                }else{
                    client = clients.get(zkAddress);
                }
            }finally {
                lock.unlock();
            }
        }
        return client;
    }

}
