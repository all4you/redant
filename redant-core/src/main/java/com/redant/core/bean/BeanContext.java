package com.redant.core.bean;

import com.redant.core.common.util.TagUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;

/**
 * Bean上下文
 * @author gris.wang
 * @date 2017-10-20
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

        final CountDownLatch latch = new CountDownLatch(loopTimes);

        class Runner implements Runnable{

            private Logger logger = LoggerFactory.getLogger(Runner.class);

            @Override
            public void run() {
                Object bean = BeanContext.getBean("userService");
                logger.info("beanName={},currentThread={}",(bean!=null?bean.getClass().getName():"null"),Thread.currentThread().getName());
                latch.countDown();
            }
        }

        TagUtil.addTag("start");
        for(int i=0;i<loopTimes;i++){
            new Thread(new Runner()).start();
        }
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        TagUtil.addTag("end");
        TagUtil.showCost("start","end");

    }
}
