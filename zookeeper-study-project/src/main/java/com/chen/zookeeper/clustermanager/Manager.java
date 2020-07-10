package com.chen.zookeeper.clustermanager;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 模拟管理者
 * @author ChenTian
 * @date 2020/7/9
 */
@RestController
@RequestMapping("/zk/cluster/manager")
public class Manager implements InitializingBean {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Value("${zookeeper.connect.url}")
    private String zookeeperUrl;
    @Value("${zookeeper.session.timeout}")
    private Integer sessionTime;

    /** 根节点路径 */
    public static String MANAGER_PATH = "/cluster_manager";
    /** zookeeper客户端*/
    private ZooKeeper zooKeeper;

    /**
     * 获取集群节点及集群节点信息
     * @return
     */
    @ResponseBody
    @GetMapping("/list")
    public String list(){
        logger.info("获取集群节点信息");
        //获取根节点下所有子节点
        List<String> children = ZookeeperUtils.getChildren(zooKeeper,MANAGER_PATH);
        if(CollectionUtil.isEmpty(children)){
            return null;
        }

        //获取所有子节点的信息
        List<String> collect = children.stream()
                .map(c -> {
                    String path = MANAGER_PATH + "/" + c;
                    String data = ZookeeperUtils.getData(zooKeeper, path);
                    return c+":"+data;
                })
                .collect(Collectors.toList());

        return JSONUtil.toJsonStr(collect);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        zooKeeper = ZookeeperUtils.getInstance(zookeeperUrl,sessionTime);
        //初始化集群管理根节点
        ZookeeperUtils.createPersistent(zooKeeper,MANAGER_PATH, "cluster");
    }



}
