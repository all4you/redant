package com.redant.mybatissist.sqlsession;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Reader;

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
	 */
	private SqlSessionFactory sqlSessionFactory;

	/**
	 * sqlSessionFactory创建完毕的标志
	 */
	private volatile boolean builded;

	/**
	 *
	 */
	private static SqlSessionHolder holder;


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
	 * @return
	 */
	public SqlSession getSqlSession() {
		if(builded()){
			return sqlSessionFactory.openSession();
		}
		return null;
	}


}
