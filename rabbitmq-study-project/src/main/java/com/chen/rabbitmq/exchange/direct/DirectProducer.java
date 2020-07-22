package com.chen.rabbitmq.exchange.direct;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import static com.chen.rabbitmq.constants.Constants.RABBITMQ_ADDRESS;

/**
 * @author ChenTian
 * @date 2020/7/16
 * direct类型交换器的生产者
 */
public class DirectProducer {

    public final static String EXCHANGE_NAME = "direct_logs";

    public static void main(String[] args) throws IOException, TimeoutException {
        //创建连接
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost(RABBITMQ_ADDRESS);
        Connection connection = connectionFactory.newConnection();

        //创建信道
        Channel channel = connection.createChannel();

        //创建交换器
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);

        //日志消息级别，作为路由键使用
        String[] services = {"error","info","warning"};
        int num = 3;
        for (int i = 0 ; i < num ; i++){
            String routeKey = services[i%3];
            String msg = services[i]+":Hello,RabbitMq"+(i+1);
            //发布消息，需要参数：交换器，路由键，其中以日志消息级别为路由键
            channel.basicPublish(EXCHANGE_NAME,routeKey,null,msg.getBytes());
            System.out.println("send "+msg);
        }

        //关闭连接
        channel.close();
        connection.close();
    }


}
