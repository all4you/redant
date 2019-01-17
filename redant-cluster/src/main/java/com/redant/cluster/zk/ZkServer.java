package com.redant.cluster.zk;

import cn.hutool.core.util.StrUtil;
import org.apache.zookeeper.server.ServerConfig;
import org.apache.zookeeper.server.ZooKeeperServerMain;
import org.apache.zookeeper.server.quorum.QuorumPeerConfig;
import org.apache.zookeeper.server.quorum.QuorumPeerConfig.ConfigException;
import org.apache.zookeeper.server.quorum.QuorumPeerMain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Properties;

/**
 * ZooKeeper服务端
 * @author houyi.wh
 * @date 2017/11/21
 *
 */
public class ZkServer {

	private static final Logger LOGGER = LoggerFactory.getLogger(ZkServer.class);

	public static String getZkAddressArgs(String[] args, ZkConfig zkConfig){
		String zkAddress = ZkServer.getZkAddress(zkConfig);
		if(args.length>0 && StrUtil.isNotBlank(args[0])){
			LOGGER.info("zkAddress is read from args");
			zkAddress = args[0];
		}
		if(StrUtil.isBlank(zkAddress)){
			System.exit(1);
		}
		return zkAddress;
	}

	public static String getZkAddress(ZkConfig zkConfig){
		return zkConfig!=null ? zkConfig.generateZkAddress() : null;
	}

	/**
	 * 通过官方的ZooKeeperServerMain启动类启动单机模式
	 * @param zkConfig 配置对象
	 * @throws ConfigException 配置异常
	 * @throws IOException IO异常
	 */
	public void startStandalone(ZkConfig zkConfig) throws ConfigException, IOException {
		Properties zkProp = zkConfig.toProp();

		QuorumPeerConfig config = new QuorumPeerConfig();
		config.parseProperties(zkProp);

		ServerConfig serverConfig = new ServerConfig();
		serverConfig.readFrom(config);

		ZooKeeperServerMain zkServer = new ZooKeeperServerMain();
		zkServer.runFromConfig(serverConfig);
	}

	/**
	 * 通过官方的QuorumPeerMain启动类启动真集群模式
	 * 会执行quorumPeer.join();
	 * 需要在不同的服务器上执行
	 * @param zkConfig 配置对象
	 * @throws ConfigException 配置异常
	 * @throws IOException IO异常
	 */
	public void startCluster(ZkConfig zkConfig) throws ConfigException, IOException {
		Properties zkProp = zkConfig.toProp();
		QuorumPeerConfig config = new QuorumPeerConfig();
		config.parseProperties(zkProp);

		QuorumPeerMain main = new QuorumPeerMain();
		main.runFromConfig(config);
	}


}