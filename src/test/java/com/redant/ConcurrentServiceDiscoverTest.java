package com.redant;

import com.redant.cluster.node.Node;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
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

        private Map<String,Node> slaveNodeMap;

        private Lock lock;

        private int slaveIndex = 0;

        public Discovery(){
            slaveNodeMap = new HashMap<String,Node>();
            Node node1 = new Node("127.0.0.1",8081);
            Node node2 = new Node("127.0.0.1",8082);
            slaveNodeMap.put(node1.getId(), node1);
            slaveNodeMap.put(node2.getId(), node2);
            lock = new ReentrantLock();
        }

        public Node discover() {
            lock.lock();
            try {
                if (slaveNodeMap.size() == 0) {
                    System.err.println("No available Node!");
                    return null;
                }
                Node[] nodes = new Node[]{};
                nodes = slaveNodeMap.values().toArray(nodes);
                // 通过CAS循环获取下一个可用服务
                if (slaveIndex>=nodes.length) {
                    slaveIndex = 0;
                }
                System.out.println("currentIndex=" + slaveIndex + ",currentThread=" + Thread.currentThread().getName());
                return nodes[slaveIndex++];
            }finally {
                lock.unlock();
            }
        }

    }



    @Test
    public void testConcurrentDiscover(){
        int loopTimes = 300;

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
