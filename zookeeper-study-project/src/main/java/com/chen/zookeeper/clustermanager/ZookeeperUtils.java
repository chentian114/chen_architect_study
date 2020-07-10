package com.chen.zookeeper.clustermanager;

import cn.hutool.json.JSONUtil;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @author ChenTian
 * @date 2020/7/9
 */
public class ZookeeperUtils {
    private static Logger logger = LoggerFactory.getLogger(ZookeeperUtils.class);

    public static ZooKeeper getInstance(String connection,Integer sessionTime){
        ZooKeeper zk = null;
        CountDownLatch countDownLatch = new CountDownLatch(1);
        try {
            zk = new ZooKeeper(connection, sessionTime, (WatchedEvent watchedEvent) -> {
                if(watchedEvent.getState().equals(Watcher.Event.KeeperState.SyncConnected)) {
                    logger.info("连接成功"+watchedEvent.getState()+",event:"+watchedEvent.getType());
                    countDownLatch.countDown();

                    //孩子节点变化
                    if(watchedEvent.getType().equals(Watcher.Event.EventType.NodeChildrenChanged)){
                        logger.info("server变化："+watchedEvent.getPath()+":"+watchedEvent.getType()+":"+watchedEvent.getState());
                    }
                }
            });
            countDownLatch.await();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return zk;
    }


    public static Stat exists(ZooKeeper zooKeeper,String path){
        try {
            Stat exists = zooKeeper.exists(path, true);
            return exists;
        } catch (KeeperException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String createPersistent(ZooKeeper zooKeeper,String path,String data){
        try {
            Stat exists = exists(zooKeeper, path);
            if(exists == null) {
                String result = zooKeeper.create(path, data.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
                logger.info("创根节点：path:" + path + ":" + result);
                return result;
            }else {
                logger.info("节点已存在："+ JSONUtil.toJsonStr(exists));
                return "success";
            }
        } catch (KeeperException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String createESNode(ZooKeeper zooKeeper,String path,String data){
        try {
            String result = zooKeeper.create(path, data.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
            logger.info("创建临时顺序节点: path"+path+":"+result);
            return result;
        } catch (KeeperException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<String> getChildren(ZooKeeper zooKeeper,String path){
        try {
            List<String> children = zooKeeper.getChildren(path, true);
            return children;
        } catch (KeeperException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取节点信息
     */
    public static String getData(ZooKeeper zooKeeper,String path){
        try {
            if(exists(zooKeeper,path) != null) {
                byte[] data = zooKeeper.getData(path, true, new Stat());
                return new String(data);
            }
        } catch (KeeperException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Stat setData(ZooKeeper zooKeeper,String path,String data){
        try {
            Stat stat = zooKeeper.setData(path, data.getBytes(), -1);
            return stat;
        } catch (KeeperException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

}
