package com.redant.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * 使用单例模式，获取配置文件中的信息
 * @author hwang
 * @since 2015-04-15
 */
public class PropertiesUtil {

	private static final Logger logger = LoggerFactory.getLogger(PropertiesUtil.class);

    private static Map<String,PropertiesUtil> propertiesUtilsHolder = null;
    
    private static Map<PropertiesUtil,Properties> propertiesMap = null;

    private volatile boolean propertiesLoaded;

    private PropertiesUtil(){
    	
    }

	/**
	 * 是否加载完毕
	 * @return
	 */
	private boolean propertiesLoaded(){
		int retryTime = 0;
		int retryTimeout = 1000;
		int sleep = 500;
		while(!propertiesLoaded && retryTime<retryTimeout){
			try {
				Thread.sleep(sleep);
				retryTime+=sleep;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return propertiesLoaded;
	}


	/**
	 * 根据Resource获取properties
	 * @param propertiesPath
	 * @return
	 */
	public static Properties getPropertiesByResource(String propertiesPath){
		InputStream inputStream = null;
		Properties properties = null;
		try{
			inputStream = PropertiesUtil.class.getResourceAsStream(propertiesPath);
			if(inputStream!=null){
				properties = new Properties();
				properties.load(inputStream);
			}
		} catch (Exception e) {
			logger.error("getInstance occur error,cause:",e);
		} finally{
			try {
				if(inputStream!=null){
					inputStream.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return properties;
	}

    /**
     * 获取实例
     * @param propertiesPath
     * @return
     * @throws Exception
     */
    public static synchronized PropertiesUtil getInstance(String propertiesPath){
    	if(null==propertiesUtilsHolder){
    		propertiesUtilsHolder = new HashMap<String,PropertiesUtil>();
    	}
    	if(null==propertiesMap){
    		propertiesMap = new HashMap<PropertiesUtil,Properties>();
    	}
    	PropertiesUtil propertiesUtil = propertiesUtilsHolder.get(propertiesPath);
    	if(null==propertiesUtil){
    		logger.info("PropertiesUtil instance is null with propertiesPath={},will new a instance directly.",propertiesPath);
			InputStream inputStream = null;
			try{
				propertiesUtil = new PropertiesUtil();
				Properties properties = new Properties();
				inputStream = PropertiesUtil.class.getResourceAsStream(propertiesPath);
				if(inputStream!=null){
					properties.load(inputStream);
					propertiesUtilsHolder.put(propertiesPath, propertiesUtil);
					propertiesMap.put(propertiesUtil, properties);

					logger.info("PropertiesUtil instance init success.");
					propertiesUtil.propertiesLoaded = true;
				}
			} catch (Exception e) {
				logger.error("getInstance occur error,cause:",e);
			} finally{
				try {
					if(inputStream!=null){
						inputStream.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
    	}
    	return propertiesUtil;
    }
    
    /**
     * 获得配置信息的String值
     * @param key
     * @return
     */
    public String getString(String key){
    	if(propertiesLoaded()){
			Properties properties = propertiesMap.get(this);
			return null != properties ? properties.getProperty(key) : null;
		}
		return null;
    }
    
    /**
     * 获得配置信息的boolean值
     * @param key
     * @return
     */
    public boolean getBoolean(String key){
    	String value = getString(key);
    	return "true".equalsIgnoreCase(value);
    }
    
    /**
     * 获得配置信息的int值
     * @param key
     * @return
     */
    public int getInt(String key,int defaultValue){
    	String value = getString(key);
    	int intValue;
    	try{
    		intValue = Integer.parseInt(value);
    	}catch(Exception e){
    		intValue = defaultValue;
    	}
    	return intValue;
    }
    
    /**
     * 获得配置信息的long值
     * @param key
     * @return
     */
    public long getLong(String key,long defaultValue){
    	String value = getString(key);
    	long longValue;
    	try{
    		longValue = Long.parseLong(value);
    	}catch(Exception e){
    		longValue = defaultValue;
    	}
    	return longValue;
    }


	public static void main(String[] args) {
		int loopTimes = 200;

		class Runner implements Runnable{

			private Logger logger = LoggerFactory.getLogger(Runner.class);

			@Override
			public void run() {
				String property = PropertiesUtil.getInstance("/redant.properties").getString("base.view.path");
				logger.info("property={},currentThread={}",property,Thread.currentThread().getName());
			}
		}

		for(int i=0;i<loopTimes;i++){
			new Thread(new Runner()).start();
		}


	}
    
}
