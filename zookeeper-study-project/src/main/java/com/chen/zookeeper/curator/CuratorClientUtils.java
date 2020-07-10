package com.chen.zookeeper.curator;

import com.chen.zookeeper.common.Constants;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

/**
 * 创建CuratorClient
 * @author ChenTian
 * @date 2020/7/7
 */
public class CuratorClientUtils {

    /**
     * 内部类单例模式
     */
    private static class SingleTon{
        private static CuratorFramework INSTANCE = CuratorFrameworkFactory.newClient(Constants.ZK_ADDRESS, Constants.S_TIMEOUT, Constants.S_TIMEOUT,
                new ExponentialBackoffRetry(1000, 3));
    }

    public static CuratorFramework getInstance(){
        SingleTon.INSTANCE.start();
        return SingleTon.INSTANCE;
    }
}
