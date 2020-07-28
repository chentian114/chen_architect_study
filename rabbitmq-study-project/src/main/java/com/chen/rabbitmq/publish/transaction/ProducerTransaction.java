package com.chen.rabbitmq.publish.transaction;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import static com.chen.rabbitmq.constants.Constants.RABBITMQ_ADDRESS;

/**
 * @author: Chentian
 * @date: Created in 2020/7/29 6:07
 * @desc 消息发布-添加事务
 */
public class ProducerTransaction {

    public static final String EXCHANGE_NAME = "transaction_test";

    public static void main(String[] args) throws IOException, TimeoutException {
        //创建连接
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost(RABBITMQ_ADDRESS);
        Connection connection = connectionFactory.newConnection();

        //创建信道
        Channel channel = connection.createChannel();

        //指定交换器及类型
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);

        //开启事务，注释事务语句，验证抛出异常不影响已发送的消息
        channel.txSelect();
        try {
            String routeKey = "error";
            int num = 3;
            for (int i = 0; i < num; i++) {
                String msg = "Hello,RabbitMq" + (i + 1);
                //发布消息，需要参数：交换器，路由键，其中以日志消息级别为路由键
                channel.basicPublish(EXCHANGE_NAME, routeKey, null, msg.getBytes());
                System.out.println("send " + msg);
                if(i == 2){
                    //注释抛出异常语句，验证发送成功
                    throw new Exception("myself exception!");
                }
            }
            //提交事务
            channel.txCommit();
        }catch (Exception e){
            //事务回滚
            channel.txRollback();
            e.printStackTrace();
        }

        channel.close();
        connection.close();
    }

}
