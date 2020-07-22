package com.chen.rabbitmq.exchange.direct;

import cn.hutool.core.thread.ThreadUtil;
import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import static com.chen.rabbitmq.constants.Constants.RABBITMQ_ADDRESS;

/**
 * @author ChenTian
 * @date 2020/7/16
 * 一个队列多个消费者，则会表现出消息在消费者之间的轮询发送。
 */
public class MulitConsumerOneQueue {

    private static final String QUEUE_NAME = "focusAll";

    public static void main(String[] args) throws IOException, TimeoutException {
        //创建连接
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost(RABBITMQ_ADDRESS);
        Connection connection = connectionFactory.newConnection();

        //声明消费者线程任务
        Runnable consumerWork = () -> {
            try {
                //创建信道，每个线程一个信道
                Channel channel = connection.createChannel();
                //创建交换器
                channel.exchangeDeclare(DirectProducer.EXCHANGE_NAME, BuiltinExchangeType.DIRECT);

                /*声明一个队列,rabbitmq，如果队列已存在，不会重复创建*/
                channel.queueDeclare(QUEUE_NAME,
                        false,false,
                        false,null);


                //所有日志级别
                String[] services={"error","info","warning"};
                //多重绑定，关注所有级别的日志
                for (String routeKey : services) {
                    channel.queueBind(QUEUE_NAME,DirectProducer.EXCHANGE_NAME, routeKey);
                }

                //消费者名字，打印输出用
                final String consumerName =  Thread.currentThread().getName();
                System.out.println(" ["+consumerName+"] Waiting for messages:");


                // 创建队列消费者
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
                channel.basicConsume(QUEUE_NAME, true, consumer);
            } catch (IOException e) {
                e.printStackTrace();
            }

        };

        //创建多个消费者线程
        int workNum = 3;
        for (int i = 0 ; i < workNum ; i++ ) {
            ThreadUtil.execute(consumerWork);
        }

    }
}
