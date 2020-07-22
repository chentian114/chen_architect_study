package com.chen.rabbitmq.exchange.direct;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import static com.chen.rabbitmq.constants.Constants.RABBITMQ_ADDRESS;

/**
 * @author ChenTian
 * @date 2020/7/16
 * 队列和交换器的多重绑定
 */
public class MulitBindConsumer {

    public static void main(String[] args) throws IOException, TimeoutException {

        //创建连接
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost(RABBITMQ_ADDRESS);
        Connection connection = connectionFactory.newConnection();

        //创建信道
        Channel channel = connection.createChannel();
        //创建交换器
        channel.exchangeDeclare(DirectProducer.EXCHANGE_NAME, BuiltinExchangeType.DIRECT);

        //声明一个随机队列
        String queueName = channel.queueDeclare().getQueue();

        //多重绑定，队列绑定到交换器上时，是允许绑定多个路由键的
        String[] services={"error","info","warning"};
        for(String routeKey:services){
            channel.queueBind(queueName,DirectProducer.EXCHANGE_NAME,routeKey);
        }
        System.out.println(" [*] Waiting for messages:");

        // 创建队列消费者
        final Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag,
                                       Envelope envelope,
                                       AMQP.BasicProperties properties,
                                       byte[] body) throws IOException {
                String message = new String(body, "UTF-8");
                System.out.println(" Received "
                        + envelope.getRoutingKey() + ":'" + message
                        + "'");
            }
        };
        channel.basicConsume(queueName, true, consumer);

    }
}
