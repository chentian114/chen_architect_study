package com.chen.zookeeper.clustermanager;

import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import org.I0Itec.zkclient.ZkClient;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;
import java.net.InetAddress;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author ChenTian
 * @date 2020/7/9
 */
public class Agent {
    private String server = "127.0.0.1:2181";
    ZkClient zkClient;
    private static Agent instance;
    private static final String rootPath = "/cluster-manger";
    private static final String servicePath = rootPath + "/service";
    private String nodePath;

    public static void main(String[] args) {
        instance = new Agent();
        instance.init();
        ThreadUtil.safeSleep(10000);
    }

    /** 初始化连接 */
    private void init() {
        zkClient = new ZkClient(server, 5000, 10000);
        System.out.println("zk连接成功" + server);
        buildRoot();
        createServerNode();

        ScheduledExecutorService executorService = new ScheduledThreadPoolExecutor(1,
                new BasicThreadFactory.Builder()
                        .namingPattern("server1-schedule-pool-%d")
                        .daemon(true)
                        .build());
        executorService.scheduleWithFixedDelay(this::task,5000,10000, TimeUnit.MILLISECONDS);
    }

    /** 构建根节点 */
    private void buildRoot() {
        if (!zkClient.exists(rootPath)) {
            zkClient.createPersistent(rootPath);
        }
    }

    /** 生成服务节点 */
    private void createServerNode() {
        nodePath = zkClient.createEphemeralSequential(servicePath, getOsInfo());
        System.out.println("创建节点:" + nodePath);
    }

    /** 更新服务节点状态 */
    private String getOsInfo() {
        JSONObject info = new JSONObject();
        info.put("lastUpdateTime",System.currentTimeMillis());
        info.put("ip",getLocalIp());
        MemoryUsage memoryUsage = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
        info.put("memoryUsage", memoryUsage);
        info.put("usableMemorySize",(memoryUsage.getUsed()/1024 / 1024));
        info.put("maxMemorySize",(memoryUsage.getMax()/1024/1024));
        System.out.println(JSONUtil.toJsonStr(info));
        return JSONUtil.toJsonStr(info);
    }

    /** 监听服务节点状态改变 */
    private void updateServerNode() {
        zkClient.writeData(nodePath, getOsInfo());
    }


    private Object getLocalIp() {
        InetAddress addr;
        try {
            addr = InetAddress.getLocalHost();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return addr.getHostAddress();
    }

    private void task() {
        while (true) {
            updateServerNode();
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
