package com.redant;

import com.redant.cluster.slave.SlaveNode;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 并发的服务发现
 * @author gris.wang
 * @since 2017/12/1
 **/
public class ConcurrentServiceDiscoverTest {

    @Before
    public void beforeTest(){

    }

    @After
    public void afterTest(){

    }

    /*
     *  线程计数器
     * 	将线程数量初始化
     * 	每执行完成一条线程，调用countDown()使计数器减1
     * 	主线程调用方法await()使其等待，当计数器为0时才被执行
     */
    private CountDownLatch latch;

    private class Discovery{

        private Map<String,SlaveNode> slaveNodeMap;

        /**
         * 使用轮询法标记当前可用的SlaveNode
         */
        private AtomicInteger slaveIndex = new AtomicInteger(0);

        private Lock lock;

        public Discovery(){
            slaveNodeMap = new HashMap<String,SlaveNode>();
            SlaveNode slaveNode1 = new SlaveNode("127.0.0.1",8081);
            SlaveNode slaveNode2 = new SlaveNode("127.0.0.1",8082);
            slaveNodeMap.put(slaveNode1.getId(),slaveNode1);
            slaveNodeMap.put(slaveNode2.getId(),slaveNode2);
            lock = new ReentrantLock();
        }

        public SlaveNode discover() {
            lock.lock();
            try {
                if(slaveNodeMap.size()==0){
                    System.err.println("No available SlaveNode!");
                    return null;
                }
                SlaveNode[] nodes = new SlaveNode[]{};
                nodes = slaveNodeMap.values().toArray(nodes);
                if(slaveIndex.get()>=nodes.length){
                    slaveIndex.set(0);
                }
                return nodes[slaveIndex.getAndIncrement()];
            }finally {
                lock.unlock();
            }
        }

    }



    @Test
    public void testConcurrentDiscover(){
        int loopTimes = 200;

        latch = new CountDownLatch(loopTimes);

        Discovery discovery = new Discovery();

        class Runner implements Runnable{

            @Override
            public void run() {
                Object object = discovery.discover();
                System.out.println(String.format("object={%s},currentThread={%s}",(object!=null?object.toString():"null"),Thread.currentThread().getName()));
                // 执行完毕，计数器减1
                latch.countDown();
            }
        }

        for(int i=0;i<loopTimes;i++){
            new Thread(new Runner()).start();
        }

        try {
            // 主线程等待
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
