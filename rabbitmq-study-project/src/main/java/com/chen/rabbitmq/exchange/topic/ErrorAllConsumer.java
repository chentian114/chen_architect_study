package com.chen.rabbitmq.exchange.topic;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import static com.chen.rabbitmq.constants.Constants.RABBITMQ_ADDRESS;

/**
 * @author ChenTian
 * @date 2020/7/16
 */
public class ErrorAllConsumer {

    public static void main(String[] args) throws IOException, TimeoutException {

        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost(RABBITMQ_ADDRESS);
        Connection connection = connectionFactory.newConnection();

        Channel channel = connection.createChannel();

        channel.exchangeDeclare(TopicProducer.EXCHANGE_NAME, BuiltinExchangeType.TOPIC);

        //声明一个随机队列
        String queueName = channel.queueDeclare().getQueue();
        //绑定路由键，匹配以error开头的消息
        String routeKey = "error.#";
        channel.queueBind(queueName,TopicProducer.EXCHANGE_NAME,routeKey);
        System.out.println(" [*] Waiting for messages:");

        //声明消费者
        final Consumer consumer = new DefaultConsumer(channel){
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope,
                                       AMQP.BasicProperties properties, byte[] body)throws IOException {
                String message = new String(body, "UTF-8");
                System.out.println("Received "  + envelope.getRoutingKey() + "':'" + message + "'");
            }
        };
        //消费消息
        channel.basicConsume(queueName, true, consumer);

    }

}
