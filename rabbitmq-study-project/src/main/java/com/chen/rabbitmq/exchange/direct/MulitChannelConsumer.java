package com.chen.rabbitmq.exchange.direct;

import cn.hutool.core.thread.ThreadUtil;
import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import static com.chen.rabbitmq.constants.Constants.RABBITMQ_ADDRESS;

/**
 * @author ChenTian
 * @date 2020/7/16
 * 一个连接多个信道
 */
public class MulitChannelConsumer {


    public static void main(String[] args) throws IOException, TimeoutException {
        //创建连接
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost(RABBITMQ_ADDRESS);
        Connection connection = connectionFactory.newConnection();

        //声明消费者任务
        Runnable consumerWork = () -> {
            //创建一个信道，意味着每个线程单独一个信道
            try {
                final Channel channel = connection.createChannel();

                //创建交换器
                channel.exchangeDeclare(DirectProducer.EXCHANGE_NAME, BuiltinExchangeType.DIRECT);

                // 声明一个随机队列，每个线程一个队列
                String queueName = channel.queueDeclare().getQueue();

                //所有日志级别
                String[] services={"error","info","warning"};
                //多重绑定，关注所有级别的日志
                for (String routeKey : services) {
                    channel.queueBind(queueName,DirectProducer.EXCHANGE_NAME, routeKey);
                }

                //消费者名字，打印输出用
                final String consumerName =  Thread.currentThread().getName()+"-all";
                System.out.println("["+consumerName+"] Waiting for messages:");

                //声明队列消费者
                final Consumer consumer = new DefaultConsumer(channel) {
                    @Override
                    public void handleDelivery(String consumerTag,
                                               Envelope envelope,
                                               AMQP.BasicProperties properties,
                                               byte[] body) throws IOException {
                        String message = new String(body, "UTF-8");
                        System.out.println(consumerName+" Received "
                                + envelope.getRoutingKey() + ":'" + message + "'");
                    }
                };

                //消费消息
                channel.basicConsume(queueName, true, consumer);
            } catch (IOException e) {
                e.printStackTrace();
            }


        };

        int num = 3;
        for ( int i = 0 ; i < num ; i++){
            ThreadUtil.execute(consumerWork);
        }

    }
}
