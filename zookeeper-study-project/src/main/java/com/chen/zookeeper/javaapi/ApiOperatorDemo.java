package com.chen.zookeeper.javaapi;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONUtil;
import com.chen.zookeeper.common.Constants;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * 常用api操作
 * @author ChenTian
 * @date 2020/7/6
 */
public class ApiOperatorDemo {


    private static CountDownLatch latch = new CountDownLatch(1);
    private static ZooKeeper zk;

    /** 监听器 */
    private static Watcher watchedEventConsumer = watchedEvent ->{
        System.out.println("watch:"+watchedEvent.getState()+"-->"+watchedEvent.getType());
        if(watchedEvent.getState().equals(Watcher.Event.KeeperState.SyncConnected)){
            //首次连接状态
            if(Watcher.Event.EventType.None == watchedEvent.getType() && null == watchedEvent.getPath()){
                latch.countDown();
                System.out.println("连接成功:"+watchedEvent.getState()+"-->"+watchedEvent.getType());
            }
            else if(Watcher.Event.EventType.NodeDataChanged.equals(watchedEvent.getType())){
                //watch是一次性的，只会被调用一次，在后面再次修改节点，watch是不会被调用的
                System.out.println("修改数据触发："+watchedEvent.getPath());
            }
            else if(Watcher.Event.EventType.NodeCreated.equals(watchedEvent.getType())){
                System.out.println("创建节点触发："+watchedEvent.getPath());
            }
            else if(Watcher.Event.EventType.NodeDeleted.equals(watchedEvent.getType())){
                System.out.println("节点删除触发："+watchedEvent.getPath());
            }
            else if(Watcher.Event.EventType.NodeChildrenChanged.equals(watchedEvent.getType())){
                System.out.println("子节点列表发生变更触发："+watchedEvent.getPath());
            }
        }
    };

    public static void main(String[] args) {
        try {
            zk = new ZooKeeper(Constants.ZK_ADDRESS,Constants.S_TIMEOUT,watchedEventConsumer);
            //等待
            latch.await();

            String nodePath = "/node1";
            //获取数据，触发修改数据watch
            System.out.println("getData:"+new String(zk.getData(nodePath, true, new Stat())));

            String result = zk.create(nodePath, "a".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
            System.out.println("result:"+result);

            //修改数据
            // -1 不需要管版本号
            zk.setData(nodePath,("val1"+ RandomUtil.randomInt()).getBytes(),-1);
            zk.setData(nodePath,("val1"+ RandomUtil.randomInt()).getBytes(),-1);
            //如果需要每次修改都触发watch，需要再进行一次查询
            System.out.println("setData:改变后的值：-->"+new String(zk.getData(nodePath,true,new Stat())));

            zk.setData(nodePath,("val1"+ RandomUtil.randomInt()).getBytes(),-1);
            //如果需要每次修改都触发watch，需要再进行一次查询
            System.out.println("setData:改变后的值：-->"+new String(zk.getData(nodePath,true,new Stat())));

            //触发子节点列表变更watch
            zk.getChildren(nodePath, true);

            //创建子节点
            String cPath = nodePath+"/child1";
            Stat exists1 = zk.exists(cPath, true);
            if(exists1 == null) {
                zk.create(cPath, ("cval1" + RandomUtil.randomInt()).getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
                System.out.println("create child path:"+cPath+" "+new String(zk.getData(cPath,true,new Stat())));
            }

            //修改子节点
            zk.setData(cPath,("cval1" + RandomUtil.randomInt()).getBytes(), -1);
            System.out.println("setData child path:"+cPath+" "+new String(zk.getData(cPath,true,new Stat())));

            //触发子节点列表变更watch
            List<String> children = zk.getChildren(nodePath, true);
            System.out.println("children list"+ JSONUtil.toJsonStr(children));

            //删除子节点
            zk.delete(cPath,-1);

            //创建节点
            String nodePath3 = "/node3";
            //触发创建节点watch
            Stat exists = zk.exists(nodePath3, true);
            if(exists == null) {
                zk.create(nodePath3, ("val3" + RandomUtil.randomInt()).getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
                System.out.println("create: "+nodePath3+":"+new String(zk.getData(nodePath3,true,new Stat())));
            }

            //删除节点
            zk.delete(nodePath3,-1);
            System.out.println("delete "+nodePath3);

        } catch (IOException | InterruptedException | KeeperException e) {
            e.printStackTrace();
        }

    }




}
