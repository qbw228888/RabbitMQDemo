package com.halden;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class RabbitMQListener {
    /**
     * 配置consumerACK的流程
     * 1.设置手动签收，在yaml文件中
     * 2.如果处理成功，调用channel的basicACK方法进行签收
     * 3.如果处理失败，调用channel的basicNack方法进行拒收
     */

    /**
     * 配置消费端限流的流程
     * 1.必须是手动签收
     * 2.配置prefetch属性，等于几就是同时进入几条消息
     */

    @RabbitListener(queues = "boot direct queue 1")
    public void listenQueue1(Message message, Channel channel) throws IOException {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        try {
            System.out.println(new String(message.getBody()));
            System.out.println("处理成功");
            channel.basicAck(deliveryTag,true);
        } catch (Exception e) {
            /**
             * 第三个参数的意思是重回队列，设置为true则拒收的消息重新回到队列中,broker就会重新发送消息
             */
            channel.basicNack(deliveryTag,true,true);
        }
    }

    @RabbitListener(queues = "boot direct queue 2")
    public void listenQueue2(Message message, Channel channel) throws IOException {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        try {
            System.out.println(new String(message.getBody()));
            System.out.println("处理成功");
            channel.basicAck(deliveryTag,true);
        } catch (Exception e) {
            /**
             * 第三个参数的意思是重回队列，设置为true则拒收的消息重新回到队列中,broker就会重新发送消息
             */
            channel.basicNack(deliveryTag,true,true);
        }
    }

    //延时队列监听器
    @RabbitListener(queues = "DLX queue")
    public void listenDLXQueue(Message message, Channel channel) throws IOException {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        try {
            System.out.println(new String(message.getBody()));
            System.out.println("延时队列处理成功");
            channel.basicAck(deliveryTag,true);
        } catch (Exception e) {
            /**
             * 第三个参数的意思是重回队列，设置为true则拒收的消息重新回到队列中,broker就会重新发送消息
             */
            channel.basicNack(deliveryTag,true,true);
        }
    }
}
