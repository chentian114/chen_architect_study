package com.chen.zookeeper.zkclient;

import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONUtil;
import com.chen.zookeeper.common.Constants;
import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;

import java.util.List;

/**
 * @author ChenTian
 * @date 2020/7/7
 */
public class ZkClientApiOperatorDemo {


    private static ZkClient getInstance(){
        return new ZkClient(Constants.ZK_ADDRESS,Constants.S_TIMEOUT);
    }

    private static IZkDataListener dataListener = new IZkDataListener() {
        @Override
        public void handleDataChange(String s, Object o) throws Exception {
            System.out.println("节点数据变更触发：节点名称："+s+"->节点修改后的值"+o);
        }

        @Override
        public void handleDataDeleted(String s) throws Exception {
            System.out.println("节点删除触发："+s);
        }
    };

    private static IZkChildListener childListener = (s, list) -> System.out.println("子节点列表变更触发："+s+"变更后的列表："+JSONUtil.toJsonStr(list));


    public static void main(String[] args) {
        ZkClient zkClient = getInstance();

        String nodePath = "/zkClient1";

        //节点变更watch
        zkClient.subscribeDataChanges(nodePath,dataListener);
        //子节点变更watch
        zkClient.subscribeChildChanges(nodePath,childListener);

        boolean exists = zkClient.exists(nodePath);
        //创建节点
        if(!exists) {
            zkClient.createPersistent(nodePath, "val" + RandomUtil.randomInt());
        }

        //查询节点
        Object readData = zkClient.readData(nodePath);
        System.out.println("readData:"+readData);

        //设置节点
        zkClient.writeData(nodePath,"val"+ RandomUtil.randomInt());

        //创建多级节点
        String cPath = nodePath+"/child1";
        zkClient.createPersistent(cPath,"val"+ RandomUtil.randomInt());

        //获取子节点
        List<String> children = zkClient.getChildren(nodePath);
        System.out.println("childList:"+ JSONUtil.toJsonStr(children));


        //递归删除节点
        zkClient.deleteRecursive(nodePath);

        System.out.println("=============end=============");
        ThreadUtil.safeSleep(2000);
    }



}
