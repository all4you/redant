package com.redant.core.common.html;

import cn.hutool.core.util.ReflectUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author houyi.wh
 * @date 2017/12/1
 **/
public class HtmlMakerFactory {

    private volatile static HtmlMakerFactory factory;

    private Map<HtmlMakerEnum,HtmlMaker> htmlMakerMap;

    private final Lock lock;

    private HtmlMakerFactory(){
        htmlMakerMap = new ConcurrentHashMap<>();
        lock = new ReentrantLock();
    }

    /**
     * 获取工厂实例
     */
    public static HtmlMakerFactory instance(){
        if(factory==null){
            synchronized (HtmlMakerFactory.class) {
                if (factory==null) {
                    factory = new HtmlMakerFactory();
                }
            }
        }
        return factory;
    }

    /**
     * 创建HtmlMaker实例
     */
    public HtmlMaker build(HtmlMakerEnum type,Class<? extends HtmlMaker> clazz){
        if(type==null){
            return null;
        }else{
            HtmlMaker htmlMaker = htmlMakerMap.get(type);
            if(htmlMaker==null){
                lock.lock();
                try {
                    if(!htmlMakerMap.containsKey(type)) {
                        htmlMaker = ReflectUtil.newInstance(clazz);
                        htmlMakerMap.putIfAbsent(type,htmlMaker);
                    }else{
                        htmlMaker = htmlMakerMap.get(type);
                    }
                }finally {
                    lock.unlock();
                }
            }
            return htmlMaker;
        }
    }

    public static void main(String[] args) {
        int loopTimes = 200;

        class Runner implements Runnable{

            private Logger logger = LoggerFactory.getLogger(Runner.class);

            @Override
            public void run() {
                HtmlMakerFactory factory = HtmlMakerFactory.instance();
                logger.info("factory={},currentThread={}",(factory!=null?factory.getClass().getName():"null"),Thread.currentThread().getName());
            }
        }

        for(int i=0;i<loopTimes;i++){
            new Thread(new Runner()).start();
        }
    }

}
