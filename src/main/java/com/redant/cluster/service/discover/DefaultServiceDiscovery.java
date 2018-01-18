package com.redant.cluster.service.discover;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.redant.cluster.slave.SlaveNode;
import com.redant.zk.ZkClient;
import com.redant.zk.ZkNode;
import io.netty.util.CharsetUtil;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 服务发现
 * @author gris.wang
 * @since 2017/11/20
 **/
public class DefaultServiceDiscovery implements ServiceDiscovery {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultServiceDiscovery.class);

    private CuratorFramework client;

    private Map<String,SlaveNode> slaveNodeMap;

    /**
     * 使用轮询法标记当前可用的SlaveNode
     */
    private AtomicInteger slaveIndex = new AtomicInteger(0);

    public DefaultServiceDiscovery(String zkServerAddress){
        client = ZkClient.getClient(zkServerAddress);
        slaveNodeMap = new HashMap<>();
    }

    @Override
    public void watchSlave() {
        if(client==null){
            throw new IllegalArgumentException("param illegal with client={null}");
        }
        try {
            initSlaveNode();
            PathChildrenCache watcher = new PathChildrenCache(
                    client,
                    ZkNode.ROOT_NODE_PATH,
                    true
            );
            watcher.getListenable().addListener(new SlaveNodeWatcher());
            watcher.start(PathChildrenCache.StartMode.BUILD_INITIAL_CACHE);
        }catch(Exception e){
            LOGGER.error("watchSlave error cause:",e);
        }
    }


    private void initSlaveNode(){
        try {
            if(client.checkExists().forPath(ZkNode.ROOT_NODE_PATH)!=null){
                List<String> children = client.getChildren().forPath(ZkNode.ROOT_NODE_PATH);
                for(String child : children){
                    String childPath = ZkNode.ROOT_NODE_PATH+"/"+child;
                    byte[] data = client.getData().forPath(childPath);
                    SlaveNode slaveNode = SlaveNode.parse(JSON.parseObject(data,JSONObject.class));
                    if(slaveNode!=null){
                        LOGGER.info("add slaveNode={} to slaveNodeMap when init",slaveNode);
                        slaveNodeMap.put(slaveNode.getId(), slaveNode);
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error("initSlaveNode error cause:",e);
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
                LOGGER.error("get a null slaveNode with eventType={},path={},data={}",event.getType(),data.getPath(),data.getData());
            }else {
                switch (event.getType()) {
                    case CHILD_ADDED:
                        slaveNodeMap.put(slaveNode.getId(), slaveNode);
                        LOGGER.info("CHILD_ADDED with path={},data={},current slaveNode size={}", data.getPath(), new String(data.getData(),CharsetUtil.UTF_8),slaveNodeMap.size());
                        break;
                    case CHILD_REMOVED:
                        slaveNodeMap.remove(slaveNode.getId());
                        LOGGER.info("CHILD_REMOVED with path={},data={},current slaveNode size={}", data.getPath(), new String(data.getData(),CharsetUtil.UTF_8),slaveNodeMap.size());
                        break;
                    case CHILD_UPDATED:
                        slaveNodeMap.replace(slaveNode.getId(), slaveNode);
                        LOGGER.info("CHILD_UPDATED with path={},data={},current slaveNode size={}", data.getPath(), new String(data.getData(),CharsetUtil.UTF_8),slaveNodeMap.size());
                        break;
                    default:
                        break;
                }
            }
        }
    }


    @Override
    public SlaveNode discover() {
        if(client==null){
            throw new IllegalArgumentException("param illegal with client={null}");
        }
        if(slaveNodeMap.size()==0){
            LOGGER.error("No available SlaveNode!");
            return null;
        }
        SlaveNode[] nodes = new SlaveNode[]{};
        nodes = slaveNodeMap.values().toArray(nodes);
        // 通过CAS循环获取下一个可用服务，不需要加锁即能保证线程安全
        // 如果当前获取到最后一个服务了，则将指针更新为0
        slaveIndex.compareAndSet(nodes.length,0);
        return nodes[slaveIndex.getAndIncrement()];
    }


}
