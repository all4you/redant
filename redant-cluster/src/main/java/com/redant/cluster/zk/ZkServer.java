package com.redant.cluster.zk;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import org.apache.zookeeper.server.*;
import org.apache.zookeeper.server.admin.AdminServer;
import org.apache.zookeeper.server.persistence.FileTxnSnapLog;
import org.apache.zookeeper.server.quorum.QuorumPeer;
import org.apache.zookeeper.server.quorum.QuorumPeerConfig;
import org.apache.zookeeper.server.quorum.QuorumPeerConfig.ConfigException;
import org.apache.zookeeper.server.quorum.QuorumPeerMain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

/**
 * ZooKeeper服务端
 * @author houyi.wh
 * @date 2017/11/21
 *
 */
public class ZkServer {

	private static final Logger LOGGER = LoggerFactory.getLogger(ZkServer.class);

	/**
	 * zk的基础目录，用以存放运行时所需的配置文件
	 */
	public static final String BASE_ZOOKEEPER_DIR = "/zookeeper/";

	/**
	 * 每次启动时会将zk的服务端地址写在该文件中
	 */
	public static final String ZOOKEEPER_ADDRESS_CFG = BASE_ZOOKEEPER_DIR+"zk_address.cfg";

	public static final String ZOOKEEPER_STANDALONE_PROPERTIES_FILE = BASE_ZOOKEEPER_DIR+"zk_standalone.properties";

	public static final String ZOOKEEPER_CLUSTER_PROPERTIES_FILE = BASE_ZOOKEEPER_DIR+"zk_cluster_server_%d.properties";

	/**
	 * ZK服务端地址
	 */
	private static String ZK_SERVER_ADDRESS;


	public static final String getZkServerAddressWithArgs(String[] args){
		String zkServerAddress = ZkServer.getZkServerAddress();
		if(args.length>0 && StrUtil.isNotBlank(args[0])){
			LOGGER.info("zkServerAddress is read from args");
			zkServerAddress = args[0];
		}
		if(StrUtil.isBlank(zkServerAddress)){
			LOGGER.error("zkServerAddress is blank please check file={}",ZkServer.ZOOKEEPER_ADDRESS_CFG);
			System.exit(1);
		}
		return zkServerAddress;
	}

	public static final String getZkServerAddress(){
		if(StrUtil.isBlank(ZK_SERVER_ADDRESS)){
			ZK_SERVER_ADDRESS = FileUtil.readUtf8String(new File(ZOOKEEPER_ADDRESS_CFG).getPath());
		}
		return ZK_SERVER_ADDRESS;
	}


	/**
	 * 启动单机模式
	 * @param zkPropertiesPath
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws ConfigException
	 */
	public void startStandalone(String zkPropertiesPath) throws IOException, ConfigException, AdminServer.AdminServerException {
		ServerConfig config = new ServerConfig();
		config.parse(zkPropertiesPath);
		startStandalone(config);
	}


	/**
	 * 通过官方的ZooKeeperServerMain启动类启动单机模式
	 * @param config
	 * @throws IOException
	 * @throws AdminServer.AdminServerException
	 */
	public void startStandalone(ServerConfig config) throws IOException, AdminServer.AdminServerException {
		ZooKeeperServerMain main = new ZooKeeperServerMain();
		main.runFromConfig(config);
	}


	/**
	 * 启动集群模式
	 * @param zkPropertiesPath
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws ConfigException
	 */
	public void startCluster(String zkPropertiesPath) throws IOException, ConfigException{
		QuorumPeerConfig config = new QuorumPeerConfig();
		// 从配置文件读取配置
		config.parse(zkPropertiesPath);
		startFakeCluster(config);
	}


	/**
	 * 启动伪集群模式
	 * @param config
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws ConfigException
	 */
	public void startFakeCluster(QuorumPeerConfig config) throws IOException{

		ServerCnxnFactory cnxnFactory = new NIOServerCnxnFactory();
		cnxnFactory.configure(config.getClientPortAddress(), config.getMaxClientCnxns());

		QuorumPeer quorumPeer = new QuorumPeer(config.getServers(), config.getDataDir(), config.getDataLogDir(), config.getElectionAlg(), config.getServerId(), config.getTickTime(), config.getInitLimit(), config.getSyncLimit(), config.getQuorumListenOnAllIPs(), cnxnFactory, config.getQuorumVerifier());
		quorumPeer.setClientAddress(config.getClientPortAddress());
		quorumPeer.setTxnFactory(new FileTxnSnapLog(config.getDataLogDir(), config.getDataDir()));
		quorumPeer.setElectionType(config.getElectionAlg());
		quorumPeer.setMyid(config.getServerId());
		quorumPeer.setTickTime(config.getTickTime());
		quorumPeer.setMinSessionTimeout(config.getMinSessionTimeout());
		quorumPeer.setMaxSessionTimeout(config.getMaxSessionTimeout());
		quorumPeer.setInitLimit(config.getInitLimit());
		quorumPeer.setSyncLimit(config.getSyncLimit());
		quorumPeer.setQuorumVerifier(config.getQuorumVerifier(), true);
		quorumPeer.setCnxnFactory(cnxnFactory);
		quorumPeer.setZKDatabase(new ZKDatabase(quorumPeer.getTxnFactory()));
		quorumPeer.setLearnerType(config.getPeerType());
		quorumPeer.setSyncEnabled(config.getSyncEnabled());
		quorumPeer.setQuorumListenOnAllIPs(config.getQuorumListenOnAllIPs());

		quorumPeer.start();
		LOGGER.info("ZkServerCluster Started! ClientPortAddress={}", config.getClientPortAddress());
	}

	/**
	 * 通过官方的QuorumPeerMain启动类启动真集群模式
	 * 会执行quorumPeer.join();
	 * 需要在不同的服务器上执行
	 * @param config
	 * @throws IOException
	 * @throws AdminServer.AdminServerException
	 */
	public void startCluster(QuorumPeerConfig config) throws IOException, AdminServer.AdminServerException {
		QuorumPeerMain main = new QuorumPeerMain();
		main.runFromConfig(config);
	}


}