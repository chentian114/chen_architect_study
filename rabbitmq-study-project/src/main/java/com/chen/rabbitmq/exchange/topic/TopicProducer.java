package com.chen.rabbitmq.exchange.topic;

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
 */
public class TopicProducer {

    public final static String EXCHANGE_NAME = "topic_logs";

    public static void main(String[] args) throws IOException, TimeoutException {

        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost(RABBITMQ_ADDRESS);
        Connection connection = connectionFactory.newConnection();

        Channel channel = connection.createChannel();

        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC);

        //日志消息，路由键最终格式类似于：info.order.B
        String[] services = {"error","info","warning"};
        String[] modules={"user","order","email"};
        String[] servers={"A","B","C"};
        for (int i = 0 ; i < 3; i++){
            for(int j=0; j < 3; j++){
                for (int k = 0 ; k < 3; k++){
                    //声明路由键
                    String routeKey = services[i%3]+"."+modules[j%3] +"."+servers[k%3];
                    String msg = "Hello Topic_["+i+","+j+","+k+"]";
                    //发送消息
                    channel.basicPublish(EXCHANGE_NAME,routeKey,null,msg.getBytes());
                    System.out.println("[x] Send '" + routeKey +":'" + msg + "'");
                }
            }
        }

        channel.close();
        connection.close();
    }


}
