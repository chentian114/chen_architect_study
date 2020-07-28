package com.chen.rabbitmq.publish.mandatory;

import cn.hutool.core.thread.ThreadUtil;
import com.chen.rabbitmq.constants.Constants;
import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @author: Chentian
 * @date: Created in 2020/7/29 5:41
 * @desc 消息发布-失败通知消息生产者
 */
public class ProducerMandatory {

    public final static String EXCHANGE_NAME = "mandatory_test";

    public static void main(String[] args) throws IOException, TimeoutException {

        //创建连接
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost(Constants.RABBITMQ_ADDRESS);
        Connection connection = connectionFactory.newConnection();

        //创建信道
        Channel channel = connection.createChannel();
        //指定交换器及类型
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);

        //连接关闭时执行监听
        connection.addShutdownListener(c ->
            System.out.println("connection shutdown :"+c.getReason()+" message:"+c.getMessage())
        );

        //信道关闭时执行监听
        channel.addShutdownListener(c ->
                System.out.println("channel shutdown :"+c.getReason()+" message:"+c.getMessage()));

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

        String routeKey = "error";
        int num = 3;
        for (int i = 0 ; i < num ; i++){
            String msg = "Hello,RabbitMq"+(i+1);
            //发布消息，需要参数：交换器，路由键，其中以日志消息级别为路由键
            channel.basicPublish(EXCHANGE_NAME,routeKey,mandatory,null,msg.getBytes());
            System.out.println("send "+msg);
        }

        // 关闭信道和连接
        ThreadUtil.safeSleep(2000);
        channel.close();
        connection.close();
    }

}
