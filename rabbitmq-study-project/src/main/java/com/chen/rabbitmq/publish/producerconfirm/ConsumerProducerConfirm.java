package com.chen.rabbitmq.publish.producerconfirm;

import com.chen.rabbitmq.publish.mandatory.ProducerMandatory;
import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import static com.chen.rabbitmq.constants.Constants.RABBITMQ_ADDRESS;

/**
 * @author: Chentian
 * @date: Created in 2020/7/29 6:37
 * @desc
 */
public class ConsumerProducerConfirm {
    public static final String QUEUE_NAME = "producer_confirm_error";

    public static void main(String[] args) throws IOException, TimeoutException {

        //创建连接
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost(RABBITMQ_ADDRESS);
        Connection connection = connectionFactory.newConnection();

        //创建信道
        Channel channel = connection.createChannel();
        //创建交换器
        channel.exchangeDeclare(ProducerConfirm.EXCHANGE_NAME, BuiltinExchangeType.DIRECT);

        //声明队列
        channel.queueDeclare(QUEUE_NAME,false,false,
                false,null);
        //绑定，将队列和交换器通过路由键进行绑定
        String routeKey = "error";
        channel.queueBind(QUEUE_NAME,ProducerConfirm.EXCHANGE_NAME,routeKey);

        System.out.println("waiting for message....");

        //声明一个消费者
        final Consumer consumer = new DefaultConsumer(channel){
            @Override
            public void handleDelivery(String consumerTag,
                                       Envelope envelope,
                                       AMQP.BasicProperties properties,
                                       byte[] body) throws IOException {
                String message = new String(body, "UTF-8");
                System.out.println("Received["+envelope.getRoutingKey()+"]"+message);
            }
        };

        //消费者正式开始在指定队列上消费消息
        channel.basicConsume(QUEUE_NAME,true,consumer);
    }
}
