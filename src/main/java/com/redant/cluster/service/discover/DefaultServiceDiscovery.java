package com.redant.cluster.service.discover;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.redant.cluster.node.Node;
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
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 服务发现
 * @author gris.wang
 * @since 2017/11/20
 **/
public class DefaultServiceDiscovery implements ServiceDiscovery {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultServiceDiscovery.class);

    private CuratorFramework client;

    private Map<String,Node> slaveNodeMap;

    private Lock lock;

    private int slaveIndex = 0;

    public DefaultServiceDiscovery(String zkServerAddress){
        client = ZkClient.getClient(zkServerAddress);
        slaveNodeMap = new HashMap<>();
        lock = new ReentrantLock();
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
                    Node node = Node.parse(JSON.parseObject(data,JSONObject.class));
                    if(node !=null){
                        LOGGER.info("add node={} to slaveNodeMap when init", node);
                        slaveNodeMap.put(node.getId(), node);
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
            Node node = Node.parse(JSON.parseObject(data.getData(),JSONObject.class));
            if(node ==null){
                LOGGER.error("get a null node with eventType={},path={},data={}",event.getType(),data.getPath(),data.getData());
            }else {
                switch (event.getType()) {
                    case CHILD_ADDED:
                        slaveNodeMap.put(node.getId(), node);
                        LOGGER.info("CHILD_ADDED with path={},data={},current node size={}", data.getPath(), new String(data.getData(),CharsetUtil.UTF_8),slaveNodeMap.size());
                        break;
                    case CHILD_REMOVED:
                        slaveNodeMap.remove(node.getId());
                        LOGGER.info("CHILD_REMOVED with path={},data={},current node size={}", data.getPath(), new String(data.getData(),CharsetUtil.UTF_8),slaveNodeMap.size());
                        break;
                    case CHILD_UPDATED:
                        slaveNodeMap.replace(node.getId(), node);
                        LOGGER.info("CHILD_UPDATED with path={},data={},current node size={}", data.getPath(), new String(data.getData(),CharsetUtil.UTF_8),slaveNodeMap.size());
                        break;
                    default:
                        break;
                }
            }
        }
    }


    @Override
    public Node discover() {
        if(client==null){
            throw new IllegalArgumentException("param illegal with client={null}");
        }
        lock.lock();
        try {
            if (slaveNodeMap.size() == 0) {
                LOGGER.error("No available Node!");
                return null;
            }
            Node[] nodes = new Node[]{};
            nodes = slaveNodeMap.values().toArray(nodes);
            // 通过CAS循环获取下一个可用服务
            if (slaveIndex>=nodes.length) {
                slaveIndex = 0;
            }
            return nodes[slaveIndex++];
        }finally {
            lock.unlock();
        }
    }


}
