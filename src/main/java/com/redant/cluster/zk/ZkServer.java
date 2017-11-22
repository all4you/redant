package com.redant.cluster.zk;

import com.xiaoleilu.hutool.io.FileUtil;
import org.apache.zookeeper.server.*;
import org.apache.zookeeper.server.persistence.FileTxnSnapLog;
import org.apache.zookeeper.server.quorum.QuorumPeer;
import org.apache.zookeeper.server.quorum.QuorumPeerConfig;
import org.apache.zookeeper.server.quorum.QuorumPeerConfig.ConfigException;
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
	public static final String ZK_SERVER_ADDRESS = ZkConfig.instance().useCluster()?ZkServer.ZK_CLUSTER_ADDRESS:ZkServer.ZK_STANDALONE_ADDRESS;


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
	public void startStandalone(String clientPort,String dataDir,String tickTime,String maxClientCnxns) throws IOException, InterruptedException, ConfigException {  
        ServerConfig config = new ServerConfig();
        String[] paras = new String[]{clientPort,dataDir,tickTime,maxClientCnxns};
        config.parse(paras);
        startStandalone(config);
    }  
	
	/**
	 * 启动单机模式
	 * @param zkPropertiesPath
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws ConfigException
	 */
	public void startStandalone(String zkPropertiesPath) throws IOException, InterruptedException, ConfigException {
		ServerConfig config = new ServerConfig();
		config.parse(zkPropertiesPath);
		startStandalone(config);
	}


	/**
	 * 启动单机模式
	 * @param properties
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws ConfigException
	 */
	public void startStandalone(Properties properties) throws IOException, InterruptedException, ConfigException {
		QuorumPeerConfig peerConfig = new QuorumPeerConfig();
		// 从配置文件读取配置
		peerConfig.parseProperties(properties);
		startStandalone(peerConfig);
	}


	/**
	 * 启动单机模式
	 * @param peerConfig
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws ConfigException
	 */
	public void startStandalone(QuorumPeerConfig peerConfig) throws IOException, InterruptedException, ConfigException {
		ServerConfig config = new ServerConfig();
		config.readFrom(peerConfig);
		startStandalone(config);
	}


	/**
	 * 启动单机模式
	 * @param config
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws ConfigException
	 */
	public void startStandalone(ServerConfig config) throws IOException, InterruptedException, ConfigException {  
		ZooKeeperServer zkServer = new ZooKeeperServer();  
		
		zkServer.setTxnLogFactory(new FileTxnSnapLog(
				new File(config.getDataLogDir()),
				new File(config.getDataDir())));  
		zkServer.setTickTime(config.getTickTime()); 
		zkServer.setMinSessionTimeout(config.getMinSessionTimeout());
		zkServer.setMaxSessionTimeout(config.getMaxSessionTimeout());
		
		ServerCnxnFactory serverCnxnFactory = new NIOServerCnxnFactory();  
		serverCnxnFactory.configure(config.getClientPortAddress(), config.getMaxClientCnxns());  
		serverCnxnFactory.startup(zkServer);  
		logger.info("ZkServerStandalone Started! ClientPortAddress={}",config.getClientPortAddress());
	}  
	
	
	/**
	 * 启动集群模式
	 * @param zkConfigPath
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws ConfigException
	 */
	public void startCluster(String zkConfigPath) throws IOException, InterruptedException, ConfigException {  
        QuorumPeerConfig config = new QuorumPeerConfig();
        // 从配置文件读取配置
        config.parse(zkConfigPath);
        startCluster(config);
    }  
	
	/**
	 * 启动集群模式
	 * @param properties
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws ConfigException
	 */
	public void startCluster(Properties properties) throws IOException, InterruptedException, ConfigException {  
        QuorumPeerConfig config = new QuorumPeerConfig();
        // 从配置文件读取配置
        config.parseProperties(properties);
        startCluster(config);
    }  
	
	
	/**
	 * 启动集群模式
	 * @param config
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws ConfigException
	 */
	public void startCluster(QuorumPeerConfig config) throws IOException, InterruptedException, ConfigException {  
		
		ServerCnxnFactory cnxnFactory = new NIOServerCnxnFactory();  
		cnxnFactory.configure(config.getClientPortAddress(), config.getMaxClientCnxns());  
		
		QuorumPeer quorumPeer = new QuorumPeer();
		quorumPeer.setClientPortAddress(config.getClientPortAddress());
		quorumPeer.setTxnFactory(new FileTxnSnapLog(
				new File(config.getDataLogDir()),
				new File(config.getDataDir())));
		quorumPeer.setQuorumPeers(config.getServers());
		quorumPeer.setElectionType(config.getElectionAlg());
		quorumPeer.setMyid(config.getServerId());
		quorumPeer.setTickTime(config.getTickTime());
		quorumPeer.setMinSessionTimeout(config.getMinSessionTimeout());
		quorumPeer.setMaxSessionTimeout(config.getMaxSessionTimeout());
		quorumPeer.setInitLimit(config.getInitLimit());
		quorumPeer.setSyncLimit(config.getSyncLimit());
		quorumPeer.setQuorumVerifier(config.getQuorumVerifier());
		quorumPeer.setCnxnFactory(cnxnFactory);
		quorumPeer.setZKDatabase(new ZKDatabase(quorumPeer.getTxnFactory()));
		quorumPeer.setLearnerType(config.getPeerType());
		quorumPeer.setSyncEnabled(config.getSyncEnabled());
		quorumPeer.setQuorumListenOnAllIPs(config.getQuorumListenOnAllIPs());
		
		quorumPeer.start();
		logger.info("ZkServerCluster Started! ClientPortAddress={}",config.getClientPortAddress());
	}  
}