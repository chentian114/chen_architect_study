package com.chen.rabbitmq.publish.producerconfirm;

import cn.hutool.core.thread.ThreadUtil;
import com.chen.rabbitmq.constants.Constants;
import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @author: Chentian
 * @date: Created in 2020/7/29 6:46
 * @desc 消息发布-发送者确认模式-异步监听确认
 */
public class ProducerConfirmAsync {
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

        //消息发布失败监听（路由失败）
        channel.addReturnListener((replyCode, replyText, exchange, routingKey, properties, body) -> {
            String message = new String(body);
            System.out.print("返回的replyCode ："+replyCode);
            System.out.print(" 返回的replyText ："+replyText);
            System.out.print(" 返回的exchange ："+exchange);
            System.out.print(" 返回的routingKey ："+routingKey);
            System.out.println(" 返回的message ："+message);
        });

        //消息异步确认监听
        channel.addConfirmListener(new ConfirmListener() {
            @Override
            public void handleAck(long deliveryTag, boolean multiple) throws IOException {
                System.out.println("Ack: deliveryTag:"+deliveryTag
                        +",multiple:"+multiple);
            }

            @Override
            public void handleNack(long deliveryTag, boolean multiple) throws IOException {
                System.out.println("Nack: deliveryTag:"+deliveryTag
                        +",multiple:"+multiple);
            }
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

        // 关闭信道和连接
        ThreadUtil.safeSleep(2000);
        channel.close();
        connection.close();
    }
}
