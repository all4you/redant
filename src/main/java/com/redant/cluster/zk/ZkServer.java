package com.redant.cluster.zk;

import com.xiaoleilu.hutool.io.FileUtil;
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
import java.util.Properties;

/**
 * ZooKeeper服务端
 * @author gris.wang
 * @since 2017/11/21
 *
 */
public class ZkServer {

	private static final Logger logger = LoggerFactory.getLogger(ZkServer.class);

	/**
	 * ZK服务端集群模式的连接字符串
	 */
	public static final String ZK_CLUSTER_ADDRESS = FileUtil.readUtf8String(new File(ZkServer.class.getResource("/zk_cluster_address.cfg").getPath()));

	/**
	 * ZK服务端单机模式的连接字符串
	 */
	public static final String ZK_STANDALONE_ADDRESS = FileUtil.readUtf8String(new File(ZkServer.class.getResource("/zk_address.cfg").getPath()));


	/**
	 * ZK服务端地址
	 */
	public static final String ZK_SERVER_ADDRESS = ZkConfig.instance().useCluster() ? ZkServer.ZK_CLUSTER_ADDRESS : ZkServer.ZK_STANDALONE_ADDRESS;


	/**
	 * 启动单机模式
	 * @param clientPort
	 * @param dataDir
	 * @param tickTime
	 * @param maxClientCnxns
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws ConfigException
	 */
	public void startStandalone(String clientPort, String dataDir, String tickTime, String maxClientCnxns) throws IOException, AdminServer.AdminServerException  {
		ServerConfig config = new ServerConfig();
		String[] paras = new String[]{clientPort, dataDir, tickTime, maxClientCnxns};
		config.parse(paras);
		startStandaloneByMain(config);
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
		startStandaloneByMain(config);
	}


	/**
	 * 启动单机模式
	 * @param properties
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws ConfigException
	 */
	public void startStandalone(Properties properties) throws IOException, ConfigException, AdminServer.AdminServerException {
		QuorumPeerConfig peerConfig = new QuorumPeerConfig();
		// 从配置文件读取配置
		peerConfig.parseProperties(properties);
		ServerConfig config = new ServerConfig();
		config.readFrom(peerConfig);
		startStandaloneByMain(config);
	}


	/**
	 * 启动单机模式
	 * @param config
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws ConfigException
	 */
	public void startStandalone(ServerConfig config) throws IOException, InterruptedException {
		FileTxnSnapLog txnLog = null;
		ServerCnxnFactory cnxnFactory;
		try {
			txnLog = new FileTxnSnapLog(config.getDataLogDir(), config.getDataDir());
			ZooKeeperServer zkServer = new ZooKeeperServer(txnLog, config.getTickTime(), config.getMinSessionTimeout(), config.getMaxSessionTimeout(), null);
			// 创建ServerCnxnFactory
			cnxnFactory = ServerCnxnFactory.createFactory();
			cnxnFactory.configure(config.getClientPortAddress(), config.getMaxClientCnxns());
			cnxnFactory.startup(zkServer);
			logger.info("ZkServerStandalone Started! ClientPortAddress={}", config.getClientPortAddress());
			if (cnxnFactory != null) {
				cnxnFactory.join();
			}
			if (zkServer.isRunning()) {
				zkServer.shutdown();
			}
		}finally {
			if (txnLog != null) {
				txnLog.close();
			}
		}
	}

	/**
	 * 通过官方的ZooKeeperServerMain启动类启动单机模式
	 * @param config
	 * @throws IOException
	 * @throws AdminServer.AdminServerException
	 */
	public void startStandaloneByMain(ServerConfig config) throws IOException, AdminServer.AdminServerException {
		ZooKeeperServerMain main = new ZooKeeperServerMain();
		main.runFromConfig(config);
	}


	/**
	 * 启动集群模式
	 * @param zkConfigPath
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws ConfigException
	 */
	public void startCluster(String zkConfigPath) throws IOException, ConfigException{
		QuorumPeerConfig config = new QuorumPeerConfig();
		// 从配置文件读取配置
		config.parse(zkConfigPath);
		startFakeCluster(config);
	}

	/**
	 * 启动集群模式
	 *
	 * @param properties
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws ConfigException
	 */
	public void startCluster(Properties properties) throws IOException, ConfigException{
		QuorumPeerConfig config = new QuorumPeerConfig();
		// 从配置文件读取配置
		config.parseProperties(properties);
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
		logger.info("ZkServerCluster Started! ClientPortAddress={}", config.getClientPortAddress());
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