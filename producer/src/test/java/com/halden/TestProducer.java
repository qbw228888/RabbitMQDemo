package com.halden;

import com.halden.config.RabbitMQConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.core.ReturnedMessage;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class TestProducer {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Test
    public void testProduce(){
        //定义confirm模式的回调函数
        rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
            /**
             *
             * @param correlationData 相关配置信息
             * @param b 交换机是否得到信息，true代表成功
             * @param s 失败原因
             */
            @Override
            public void confirm(CorrelationData correlationData, boolean b, String s) {
                if (b == true){
                    System.out.println("交换机成功得到消息");
                } else {
                    System.out.println("发送失败，失败原因："+s);
                }
            }
        });
        //设置处理消息发送失败的模式,true表示失败则将消息返回到returnedMessage方法中的returnedMessage参数
        rabbitTemplate.setMandatory(true);
        //定义return模式的回调函数
        rabbitTemplate.setReturnsCallback(new RabbitTemplate.ReturnsCallback() {
            @Override
            public void returnedMessage(ReturnedMessage returnedMessage) {
                System.out.println(returnedMessage);
            }
        });
        //设置消息的过期时间和消息优先级
        /**
         * 如果同时设置了消息的过期时间和队列的过期时间，以短的为准
         * 队列过期后会丢弃其中的所有消息
         * 消息只有在队列顶端才会判断其是否会过期，如果过期则移除
         * 一般只设置队列的过期时间
         */
        MessagePostProcessor messagePostProcessor =  new MessagePostProcessor(){
            @Override
            public Message postProcessMessage(Message message) throws AmqpException {
                message.getMessageProperties().setExpiration("10000");//消息过期时间
                message.getMessageProperties().setPriority(5);//消息优先级
                return message;
            }
        } ;

        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME,"test1","test springboot error");
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME,"test2","test springboot log"
                //,messagePostProcessor
        );
    }

    @Test
    public void testDLX(){
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME,"testDLX","test dlx 1");
    }
}
