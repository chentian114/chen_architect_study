package com.chen.zookeeper.lock;

import cn.hutool.core.thread.ThreadUtil;
import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.stream.Collectors;

/**
 * @author ChenTian
 * @date 2020/7/10
 */
public class ZookeeperLock {
    private String ZK_ADDRESS = "127.0.0.1:2181";
    private ZkClient zkClient;
    private static final String rootPath = "/zk-lock";

    public ZookeeperLock(){
        zkClient = new ZkClient(ZK_ADDRESS,5000,20000);
        buildRoot();
    }

    /** 构建根节点 */
    private void buildRoot() {
        if(!zkClient.exists(rootPath)){
            zkClient.createPersistent(rootPath);
        }
    }

    /** 创建Lock节点*/
    private LockNode createLockNode(String lockId){
        String nodePath = zkClient.createEphemeralSequential(rootPath+"/"+lockId,"lock");
        return new LockNode(lockId,nodePath);
    }

    /** 尝试激活锁 */
    private LockNode tryActiveLock(LockNode lockNode){
        //判断当前最小节点
        List<String> children = zkClient.getChildren(rootPath);
        List<String> collect = children.stream().sorted().map(p -> rootPath + "/" + p).collect(Collectors.toList());
        String firstNodePath = collect.get(0);
        if(firstNodePath.equals(lockNode.getNodePath())){
            lockNode.setActive(true);
            return lockNode;
        }

        //获取前一个节点
        String upNodePath = collect.get(collect.indexOf(lockNode.getNodePath())-1);
        //构建监听链表
        zkClient.subscribeDataChanges(upNodePath, new IZkDataListener() {
            @Override
            public void handleDataChange(String dataPath, Object data) throws Exception {
            }

            @Override
            public void handleDataDeleted(String dataPath) throws Exception {
                // 事件处理与心跳 在同一个线程，如果Debug时占用太多时间，将导致本节点被删除，从而影响锁逻辑。
                System.out.println("节点删除："+dataPath);
                LockNode lock = tryActiveLock(lockNode);
                synchronized (lockNode){
                    if(lock.isActive()){
                        //获得锁，唤醒等待线程
                        lockNode.notify();
                    }
                }
                //去掉监听
                zkClient.unsubscribeDataChanges(upNodePath,this);
            }
        });

        return lockNode;
    }

    /** 获得锁 */
    public LockNode lock(String lockId, long timeout){
        //创建节点
        LockNode lockNode = createLockNode(lockId);
        //申请锁
        lockNode = tryActiveLock(lockNode);
        if(!lockNode.isActive()){
            try {
                //同步等待获得锁
                synchronized (lockNode){
                    //等待超时时间
                    lockNode.wait(timeout);
                }
            }catch (Exception e){
                throw new RuntimeException("lock error!");
            }
        }
        if(!lockNode.isActive()){
            throw new RuntimeException("lock error!");
        }
        return lockNode;
    }

    /** 释放锁 */
    public void unlock(LockNode lockNode){
        if(lockNode.isActive()){
            zkClient.delete(lockNode.getNodePath());
        }
    }

    /** 共享资源 */
    public static Integer value = 0;

    public static void main(String[] args) {

        //创建锁
        ZookeeperLock zkLock = new ZookeeperLock();

        //创建线程池
        ScheduledExecutorService executorService = new ScheduledThreadPoolExecutor(5,
                new BasicThreadFactory.Builder()
                        .namingPattern("lock-schedule-pool-%d")
                        .daemon(true)
                        .build());

        Runnable task = () -> {
            //申请锁
            LockNode lockNode = zkLock.lock("value", 10000);
            value += 1;
            String threadName = Thread.currentThread().getName();
            long threadId = Thread.currentThread().getId();
            System.out.println("node:"+lockNode.getNodePath()+" name:"+threadName+" id:"+threadId+" update value:"+value);
            //模拟业务处理暂停
            ThreadUtil.safeSleep(500);
            //释放锁
            zkLock.unlock(lockNode);

        };
        System.out.println("before value:"+value);
        int num = 10;
        //模拟进程竞争资源
        for (int i = 0 ; i < num ; i++) {
            executorService.submit(task);
        }

        ThreadUtil.safeSleep(20000);
        System.out.println(num+" Thread update value,after value:"+value);

    }

}
