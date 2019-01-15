package com.redant.cluster.service.register;

import cn.hutool.core.util.StrUtil;
import com.redant.cluster.slave.Node;
import com.redant.cluster.zk.ZkClient;
import com.redant.cluster.zk.ZkNode;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author houyi.wh
 * @date 2017/11/21
 **/
public class DefaultServiceRegistery implements ServiceRegistery {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultServiceRegistery.class);

    private CuratorFramework client;


    public DefaultServiceRegistery(String zkServerAddress){
        client = ZkClient.getClient(zkServerAddress);
    }


    @Override
    public void register(Node node) {
        if(client==null || node ==null){
            throw new IllegalArgumentException(String.format("param illegal with client={%s},slave={%s}",client==null?null:client.toString(), node ==null?null: node.toString()));
        }
        try {
            if(client.checkExists().forPath(ZkNode.SLAVE_NODE_PATH)==null) {
                // 创建临时节点
                client.create()
                      .creatingParentsIfNeeded()
                      .withMode(CreateMode.EPHEMERAL_SEQUENTIAL)
                      .forPath(ZkNode.SLAVE_NODE_PATH, StrUtil.utf8Bytes(node.toString()));
            }
        } catch (Exception e) {
            LOGGER.error("register slave error with slave={},cause:", node,e);
        }
    }


}
