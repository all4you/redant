package com.redant.core.bean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Bean上下文
 * @author gris.wang
 * @create 2017-10-20
 */
public class BeanContext {

    private static final BeanHolder beanHolder = BeanHolder.create();

    /**
     * 初始化bean
     */
    public static void initBeans(){
        beanHolder.initBeans();
        beanHolder.annotationInject();
    }

    /**
     * 根据bean的name获取具体的bean对象
     * @param name
     * @return
     */
    public static Object getBean(String name){
        return beanHolder.getBean(name);
    }

    /**
     * 根据bean的name获取具体的bean对象
     * @param name
     * @param clazz
     * @return
     */
    public static <T>T getBean(String name,Class<T> clazz) {
        return beanHolder.getBean(name,clazz);
    }


    public static void main(String[] args) {
        int loopTimes = 200;

        class Runner implements Runnable{

            private Logger logger = LoggerFactory.getLogger(Runner.class);

            @Override
            public void run() {
                Object bean = BeanContext.getBean("jsonSerializer");
                logger.info("beanName={},currentThread={}",(bean!=null?bean.getClass().getName():"null"),Thread.currentThread().getName());
            }
        }

        for(int i=0;i<loopTimes;i++){
            new Thread(new Runner()).start();
        }


    }
}
