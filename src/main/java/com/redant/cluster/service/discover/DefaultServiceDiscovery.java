package com.redant.cluster.service.discover;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.redant.cluster.slave.SlaveNode;
import com.redant.cluster.zk.ZkClient;
import com.redant.cluster.zk.ZkNode;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 服务发现
 * @author gris.wang
 * @since 2017/11/20
 **/
public class DefaultServiceDiscovery implements ServiceDiscovery {

    private static final Logger logger = LoggerFactory.getLogger(DefaultServiceDiscovery.class);

    private CuratorFramework client;

    private Map<String,SlaveNode> slaveNodeMap;

    private Lock lock;

    /**
     * 使用轮询法标记当前可用的SlaveNode
     */
    private AtomicInteger slaveIndex = new AtomicInteger(0);

    public DefaultServiceDiscovery(String zkServerAddress){
        client = ZkClient.getClient(zkServerAddress);
        slaveNodeMap = new ConcurrentHashMap<String,SlaveNode>();
        lock = new ReentrantLock();
    }

    @Override
    public void watchSlave() {
        if(client==null){
            throw new IllegalArgumentException(String.format("param illegal with client={%s}",client==null?null:client.toString()));
        }
        try {
            PathChildrenCache watcher = new PathChildrenCache(
                    client,
                    ZkNode.ROOT_NODE_PATH,
                    true
            );
            watcher.getListenable().addListener(new SlaveNodeWatcher());
            watcher.start(PathChildrenCache.StartMode.NORMAL);
        }catch(Exception e){
            logger.error("watchSlave error cause:",e);
        }
    }

    private class SlaveNodeWatcher implements PathChildrenCacheListener{
        @Override
        public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
            ChildData data = event.getData();
            if(data==null || data.getData()==null){
                return;
            }
            SlaveNode slaveNode = SlaveNode.parse(JSON.parseObject(data.getData(),JSONObject.class));
            if(slaveNode==null){
                logger.error("get a null slaveNode with eventType={},path={},data={}",event.getType(),data.getPath(),data.getData());
            }else {
                switch (event.getType()) {
                    case CHILD_ADDED:
                        slaveNodeMap.put(slaveNode.getId(), slaveNode);
                        logger.info("CHILD_ADDED with path={},data={},current slaveNode size={}", data.getPath(), new String(data.getData()),slaveNodeMap.size());
                        break;
                    case CHILD_REMOVED:
                        slaveNodeMap.remove(slaveNode.getId());
                        logger.info("CHILD_REMOVED with path={},data={},current slaveNode size={}", data.getPath(), new String(data.getData()),slaveNodeMap.size());
                        break;
                    case CHILD_UPDATED:
                        slaveNodeMap.replace(slaveNode.getId(), slaveNode);
                        logger.info("CHILD_UPDATED with path={},data={},current slaveNode size={}", data.getPath(), new String(data.getData()),slaveNodeMap.size());
                        break;
                    default:
                        break;
                }
            }
        }
    }


    @Override
    public SlaveNode discover() {
        lock.lock();
        try {
            if(client==null){
                throw new IllegalArgumentException(String.format("param illegal with client={%s}", client == null ? null : client.toString()));
            }
            if(slaveNodeMap.size()==0){
                logger.error("No available SlaveNode!");
                return null;
            }
            SlaveNode[] nodes = new SlaveNode[]{};
            nodes = slaveNodeMap.values().toArray(nodes);
            if(slaveIndex.get()>=nodes.length){
                slaveIndex.set(0);
            }
            SlaveNode slaveNode = nodes[slaveIndex.getAndIncrement()];
            return slaveNode;
        }finally {
            lock.unlock();
        }
    }


}
