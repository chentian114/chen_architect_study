# Rabbitmq学习实践

## 交换器

Direct Exchange
- 生产者和消费者一般用法
- 队列和交换器的多重绑定
- 一个连接多个信道
- 一个队列多个消费者

[exchange案例位置](./src/main/java/com/chen/rabbitmq/exchange)

Direct Exchange：
- DirectProducer：direct类型交换器的生产者
- NormalConsumer：普通的消费者
- MulitBindConsumer：队列绑定到交换器上时，是允许绑定多个路由键的，也就是多重绑定
- MulitChannelConsumer：一个连接下允许有多个信道
- MulitConsumerOneQueue：一个队列多个消费者，则会表现出消息在消费者之间的轮询发送。

Fanout Exchange
- 生产者和消费者一般用法
- 看路由键有无影响？
- 消息广播到绑定的队列
- 通过测试表明，不管如何调整生产者和消费者的路由键，都对消息的接受没有影响。

Topic Exchange
- 路由键中的“*”和“#”
- 生产者和消费者使用Topic，看路由键中的“*”和“#”的实际效果



## 参考

享学课堂