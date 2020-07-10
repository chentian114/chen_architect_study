package com.chen.zookeeper.curator;

import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONUtil;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.transaction.CuratorTransactionResult;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;

import java.util.Collection;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;

import static java.util.concurrent.Executors.newFixedThreadPool;

/**
 * @author ChenTian
 * @date 2020/7/7
 */
public class CuratorOperatorDemo {

    public static void main(String[] args) throws Exception {

        CuratorFramework curatorFramework = CuratorClientUtils.getInstance();
        System.out.println("连接成功......");

        String nodePath = "/curator1";

        Stat stat = curatorFramework.checkExists().forPath(nodePath);
        //创建节点
        if(stat == null) {
            String result = curatorFramework.create().creatingParentsIfNeeded()
                    .withMode(CreateMode.PERSISTENT)
                    .forPath(nodePath, ("val" + RandomUtil.randomInt()).getBytes());
            System.out.println("create " + result);
        }

        //创建多级节点
        String cPath = nodePath+"/child1";
        stat = curatorFramework.checkExists().forPath(cPath);
        if(stat == null) {
            String result = curatorFramework.create().creatingParentsIfNeeded()
                    .withMode(CreateMode.PERSISTENT)
                    .forPath(cPath, ("val" + RandomUtil.randomInt()).getBytes());
            System.out.println("create " + result);
        }

        //查询
        byte[] bytes = curatorFramework.getData().storingStatIn(stat).forPath(nodePath);
        System.out.println("getData:"+new String(bytes));

        //更新
        stat = curatorFramework.setData().forPath(nodePath, ("val" + RandomUtil.randomInt()).getBytes());
        System.out.println("update stat:"+ JSONUtil.toJsonStr(stat));

        bytes = curatorFramework.getData().storingStatIn(stat).forPath(nodePath);
        System.out.println("getData:"+new String(bytes));

        //删除节点
        curatorFramework.delete().deletingChildrenIfNeeded().forPath(nodePath);
        System.out.println("delete path:"+nodePath);


        String nodePath2 = "/curator2";
        //异步操作
        ExecutorService service= newFixedThreadPool(1);
        CountDownLatch countDownLatch=new CountDownLatch(1);
        curatorFramework.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).
                inBackground((curatorFramework1, curatorEvent) -> {
                    System.out.println(Thread.currentThread().getName()+"->resultCode:"+curatorEvent.getResultCode()+"->"
                            +curatorEvent.getType());
                    countDownLatch.countDown();
                },service).forPath(nodePath2,("123"+RandomUtil.randomInt()).getBytes());
        countDownLatch.await();
        service.shutdown();

        String nodePath3 = "/trans";
        //事务操作（curator独有的）
        Collection<CuratorTransactionResult> resultCollections=curatorFramework.inTransaction().create()
                .forPath(nodePath3,"111".getBytes())
                .and()
                .setData()
//                .forPath("/curator","111".getBytes())
                .forPath(nodePath2,("123"+RandomUtil.randomInt()).getBytes())
                .and()
                .commit();
        for (CuratorTransactionResult result:resultCollections){
            System.out.println(result.getForPath()+"->"+result.getType());
        }

        curatorFramework.delete().deletingChildrenIfNeeded().forPath(nodePath2);
        curatorFramework.delete().deletingChildrenIfNeeded().forPath(nodePath3);


        System.out.println("============end=================");
        ThreadUtil.safeSleep(2000);
    }
}
