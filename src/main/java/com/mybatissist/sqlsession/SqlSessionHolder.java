package com.mybatissist.sqlsession;

import com.mybatissist.cache.CacheContainer;
import com.mybatissist.cache.CacheType;
import com.mybatissist.config.Config;
import com.xiaoleilu.hutool.util.RandomUtil;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

/**
 * SqlSessionHolder
 * @author gris.wang
 * @create 2017-10-20
 *
 */
public class SqlSessionHolder {

	private static final Logger logger = LoggerFactory.getLogger(SqlSessionHolder.class);

	/**
	 * 产生sqlSession的工厂
	 * sqlSessionFactory需要保持全局唯一，所以需要实现为单例
	 */
	private SqlSessionFactory sqlSessionFactory;

	/**
	 * sqlSessionFactory创建完毕的标志
	 */
	private volatile boolean builded;

	/**
	 * 实例
	 */
	private static SqlSessionHolder holder;

	/**
	 * SqlSession缓存
	 */
	private static SqlSessionCache sqlSessionCache;

	/**
	 * 私有构造方法
	 */
	private SqlSessionHolder(){

	}

	/**
	 * sqlSessionFactory是否创建完毕
	 * @return
	 */
	private boolean builded(){
		while(!builded){
			buildFactory();
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return builded;
	}


	//=====================================================

	/**
	 * 创建实例
	 * @return
	 */
	public static SqlSessionHolder create(){
		synchronized (SqlSessionHolder.class){
			if(holder==null){
				holder = new SqlSessionHolder();
			}
			return holder;
		}
	}

	/**
	 * 创建sqlSessionFactory
	 */
	public void buildFactory(){
		synchronized (SqlSessionHolder.class){
			if(sqlSessionFactory==null){
				logger.info("Start to build sqlSessionFactory...");
				try {
					Reader reader  = Resources.getResourceAsReader("mybatis-config.xml");
					sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
					reader.close();

					if(Config.instance().cacheSqlSession()) {
						// 操作sqlSession缓存
						sqlSessionCache = new SqlSessionCache(sqlSessionFactory);
						sqlSessionCache.cacheSqlSession();
					}

					logger.info("BuildFactory Success!");
				}catch (IOException e){
					logger.error("BuildFactory error,cause:",e);
				}
			}
			builded = true;
		}
	}


	/**
	 * 获取SqlSessionFactory
	 * @return
	 */
	public SqlSessionFactory getSqlSessionFactory() {
		if(builded()){
			return sqlSessionFactory;
		}
		return null;
	}

	/**
	 * 获取SqlSession
	 * @param autoCommit
	 * @return
	 */
	public SqlSession getSqlSession(boolean autoCommit) {
		if(builded()){
			SqlSession sqlSession;
			// 如果设置了使用SQLSession缓存
			if(Config.instance().cacheSqlSession() && sqlSessionCache!=null) {
				sqlSession = sqlSessionCache.getSqlSession(autoCommit);
				if (sqlSession != null) {
					logger.debug("get sqlSession from sqlSessionCache");
					return sqlSession;
				}
			}
			sqlSession = sqlSessionFactory.openSession(autoCommit);
			logger.debug("create sqlSession by sqlSessionFactory");
			if(Config.instance().cacheSqlSession() && sqlSessionCache!=null) {
				sqlSessionCache.addSqlSession(sqlSession,autoCommit);
				logger.debug("add sqlSession to sqlSessionCache");
			}
			return sqlSession;
		}
		return null;
	}


	private static class SqlSessionCache{

		private SqlSessionCache(){

		}

		/**
		 * 缓存的个数
		 */
		private int capacity = 10;

		/**
		 * 缓存的超时时间(毫秒数)
		 */
		private long timeout = 10*1000;

		private SqlSessionFactory sqlSessionFactory;

		private Map<Boolean,String> keyWrapper;

		/**
		 * 缓存容器
		 */
		private CacheContainer<String,SqlSession> cacheContainer;

		/**
		 * 构造函数
		 * @param sqlSessionFactory
		 */
		public SqlSessionCache(SqlSessionFactory sqlSessionFactory){
			this.sqlSessionFactory = sqlSessionFactory;
			this.cacheContainer = new CacheContainer(capacity,timeout, CacheType.FIFO);
			this.keyWrapper = new HashMap<Boolean,String>(){{
				this.put(Boolean.TRUE,"autoCommit");
				this.put(Boolean.FALSE,"noAutoCommit");
			}};
		}

		/**
		 * 一次性缓存SqlSession
		 */
		public void cacheSqlSession(){
			for(int index=0;index<this.capacity;index++){
				SqlSession autoCommitSqlSession = this.sqlSessionFactory.openSession(true);
				SqlSession noAutoCommitSqlSession = this.sqlSessionFactory.openSession();
				this.cacheContainer.put(this.keyWrapper.get(Boolean.TRUE)+index,autoCommitSqlSession);
				this.cacheContainer.put(this.keyWrapper.get(Boolean.FALSE)+index,noAutoCommitSqlSession);
			}
		}

		/**
		 * 添加一个SqlSession到缓存中去
		 * @param sqlSession
		 */
		public void addSqlSession(SqlSession sqlSession,boolean autoCommit){
			// 随机获取一个[0,capacity)之间的数字
			Integer index = RandomUtil.randomInt(this.capacity);
			this.cacheContainer.put(this.keyWrapper.get(Boolean.valueOf(autoCommit))+index,sqlSession);
		}

		/**
		 * 随机获取一个SqlSession
		 * @return
		 */
		public SqlSession getSqlSession(boolean autoCommit){
			// 随机获取一个[0,capacity)之间的数字
			Integer index = RandomUtil.randomInt(this.capacity);
			return this.cacheContainer.get(this.keyWrapper.get(Boolean.valueOf(autoCommit))+index);
		}
	}

}
