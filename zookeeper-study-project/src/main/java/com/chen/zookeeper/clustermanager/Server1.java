package com.chen.zookeeper.clustermanager;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.apache.zookeeper.ZooKeeper;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author ChenTian
 * @date 2020/7/9
 */
@Service
public class Server1 implements InitializingBean {
    @Value("${zookeeper.connect.url}")
    private String zookeeperUrl;
    @Value("${zookeeper.session.timeout}")
    private Integer sessionTime;

    private ZooKeeper zooKeeper;

    private String server = "/server";
    /**实际节点path */
    private String realPath;

    @Override
    public void afterPropertiesSet() throws Exception {
        zooKeeper = ZookeeperUtils.getInstance(zookeeperUrl, sessionTime);

        JSONObject data = new JSONObject();
        data.put("ip","192.168.1.101");
        data.put("cpu", 10);
        data.put("memory",10);
        data.put("disk",10);

        String serverPath = Manager.MANAGER_PATH+server;

        //创建临时顺序节点
        realPath = ZookeeperUtils.createESNode(zooKeeper, serverPath, JSONUtil.toJsonStr(data));

        //创建线程池
        ScheduledExecutorService executorService = new ScheduledThreadPoolExecutor(1,
                        new BasicThreadFactory.Builder()
                        .namingPattern("server1-schedule-pool-%d")
                        .daemon(true)
                        .build());

        //上传/更新信息
        Runnable task = () -> {
            System.out.println(realPath+"上传信息....");
            data.put("ip","192.168.1.101");
            data.put("cpu", RandomUtil.randomInt(10,100));
            data.put("memory",RandomUtil.randomInt(10,100));
            data.put("disk",RandomUtil.randomInt(10,100));
            ZookeeperUtils.setData(zooKeeper,realPath,JSONUtil.toJsonStr(data));
        };

        //定时上传
        executorService.scheduleWithFixedDelay(task,5000,10000, TimeUnit.MILLISECONDS);
    }


}
