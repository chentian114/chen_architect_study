package com.chen.zookeeper.zkclient;

import com.chen.zookeeper.common.Constants;
import org.I0Itec.zkclient.ZkClient;

/**
 * 创建会话连接
 * @author ChenTian
 * @date 2020/7/7
 */
public class SesseionDemo {

    public static void main(String[] args) {

        ZkClient zkClient = new ZkClient(Constants.ZK_ADDRESS,Constants.S_TIMEOUT);

        System.out.println(zkClient+" success");
    }

}
