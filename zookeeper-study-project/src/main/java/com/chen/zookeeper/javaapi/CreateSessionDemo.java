package com.chen.zookeeper.javaapi;

import com.chen.zookeeper.common.Constants;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * 连接zookeeper
 * @author ChenTian
 * @date 2020/7/6
 */
public class CreateSessionDemo {

    private static CountDownLatch latch = new CountDownLatch(1);

    public static void main(String[] args) throws IOException, InterruptedException {

        ZooKeeper zk = new ZooKeeper(Constants.ZK_ADDRESS, Constants.S_TIMEOUT, watchedEvent -> {
            //如果当前的连接状态是连接成功的，那么通过计数器去控制
            if(watchedEvent.getState() == Watcher.Event.KeeperState.SyncConnected){
                latch.countDown();
                System.out.println("连接成功："+watchedEvent.getState());
            }
        });

        //等待
        latch.await();

        System.out.println(zk.getState());
    }

}
