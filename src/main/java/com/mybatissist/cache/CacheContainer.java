package com.mybatissist.cache;

import com.xiaoleilu.hutool.cache.Cache;
import com.xiaoleilu.hutool.cache.CacheUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 缓存容器
 * @author gris.wang
 * @since 2017/11/1
 */
public class CacheContainer<K,V> {

    private static final Logger logger = LoggerFactory.getLogger(CacheContainer.class);

    private CacheContainer(){

    }

    /**
     * 缓存的个数
     */
    private int capacity;

    /**
     * 缓存的超时时间(毫秒数)
     */
    private long timeout;

    /**
     * 缓存
     */
    private Cache<K,V> cache;


    //==================================


    /**
     * 构造函数
     * @param capacity
     * @param timeout
     * @param cacheType
     */
    public CacheContainer(int capacity,long timeout,CacheType cacheType){
        this.capacity = capacity;
        this.timeout = timeout;
        switch (cacheType){
            case FIFO:{
                cache = CacheUtil.newFIFOCache(this.capacity,this.timeout);
            }break;
            case LFU:{
                cache = CacheUtil.newLFUCache(this.capacity,this.timeout);
            }break;
            case LRU:{
                cache = CacheUtil.newLRUCache(this.capacity,this.timeout);
            }break;
            default:{
                cache = CacheUtil.newFIFOCache(this.capacity,this.timeout);
            }break;
        }
    }


    /**
     * 添加
     * @param key
     * @param val
     */
    public void put(K key,V val){
        cache.put(key,val);
        logger.debug("put cache size={}",cache.size());
    }


    /**
     * 获取
     * @param key
     * @return
     */
    public V get(K key){
        logger.debug("get cache key={},size={}",key,cache.size());
        return cache.get(key);
    }


}
