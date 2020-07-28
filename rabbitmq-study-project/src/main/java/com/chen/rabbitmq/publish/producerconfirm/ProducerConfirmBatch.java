package com.chen.rabbitmq.publish.producerconfirm;

import com.chen.rabbitmq.constants.Constants;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @author: Chentian
 * @date: Created in 2020/7/29 6:27
 * @desc 消息发布-发送者确认模式-批量确认
 */
public class ProducerConfirmBatch {
    public static final String EXCHANGE_NAME = "producer_confirm_test";

    public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {

        //创建连接
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost(Constants.RABBITMQ_ADDRESS);
        Connection connection = connectionFactory.newConnection();

        //创建信道
        Channel channel = connection.createChannel();
        //指定交换器及类型
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);

        //消息发布失败监听
        channel.addReturnListener((replyCode, replyText, exchange, routingKey, properties, body) -> {
            String message = new String(body);
            System.out.print("返回的replyCode ："+replyCode);
            System.out.print(" 返回的replyText ："+replyText);
            System.out.print(" 返回的exchange ："+exchange);
            System.out.print(" 返回的routingKey ："+routingKey);
            System.out.println(" 返回的message ："+message);
        });

        //开启失败通知
        boolean mandatory = true;

        //启用发送者确认模式
        channel.confirmSelect();

        String routeKey = "error";
        int num = 3;
        for (int i = 0 ; i < num ; i++){
            String msg = "Hello,RabbitMq"+(i+1);
            //发布消息，需要参数：交换器，路由键，其中以日志消息级别为路由键
            channel.basicPublish(EXCHANGE_NAME,routeKey,mandatory,null,msg.getBytes());
            System.out.println("send "+msg);
        }

        //发送者批量确认，同步方式，使用同步方式等所有的消息发送之后才会执行后面代码，只要有一个消息未到达交换器就会抛出IOException异常。
        channel.waitForConfirmsOrDie();

        // 关闭信道和连接
        channel.close();
        connection.close();
    }

}
