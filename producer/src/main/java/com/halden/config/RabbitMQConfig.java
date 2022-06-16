package com.halden.config;

import org.mockito.internal.stubbing.BaseStubbing;
import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class RabbitMQConfig {

    public final static String EXCHANGE_NAME = "boot direct change";
    public final static String EXCHANGE_DLX = "DLX exchange";
    public final static String QUEUE1_NAME = "boot direct queue 1";
    public final static String QUEUE2_NAME = "boot direct queue 2";
    public final static String QUEUE_TEST_DLX = "DLX test queue";
    public final static String QUEUE_DLX = "DLX queue";
    //配置交换机
    @Bean("bootExchange")
    public Exchange bootExchange(){
        return ExchangeBuilder.directExchange(EXCHANGE_NAME).autoDelete().build();
    }

    //配置死信交换机
    @Bean("DLXExchange")
    public Exchange DLXExchange(){
        return ExchangeBuilder.directExchange(EXCHANGE_DLX).autoDelete().build();
    }
    //配置队列
    @Bean("bootQueue1")
    public Queue bootQueue1(){
        //ttl可以设置过期时间,ms为单位
        return QueueBuilder.nonDurable(QUEUE1_NAME).autoDelete()
                //.ttl(600000)
                .build();
    }
    @Bean("bootQueue2")
    public Queue bootQueue2(){
        return QueueBuilder.nonDurable(QUEUE2_NAME).autoDelete()
                .maxPriority(10)//一般最大优先级设置为10
                //.lazy()//惰性队列
                .build();
    }

    //配置延时队列和死信队列
    @Bean("DLXTestQueue")//延时队列
    public Queue DLXTestQueue(){
        return QueueBuilder.nonDurable(QUEUE_TEST_DLX).autoDelete()
                .deadLetterExchange(EXCHANGE_DLX)//绑定的死信队列
                .deadLetterRoutingKey("dlx")//绑定的死信队列的路由key
                .ttl(100000)
                .build();
    }
    @Bean("DLXQueue")
    public Queue DLXQueue(){
        return QueueBuilder.nonDurable(QUEUE_DLX).autoDelete().build();
    }

    //绑定交换机和队列
    @Bean
    public Binding bindingQueue1ToExchange(@Qualifier("bootQueue1") Queue queue, @Qualifier("bootExchange") Exchange exchange){
        return BindingBuilder.bind(queue).to(exchange).with("test1").noargs();
    }

    @Bean
    public Binding bindingQueue2ToExchange(@Qualifier("bootQueue2") Queue queue, @Qualifier("bootExchange") Exchange exchange){
        return BindingBuilder.bind(queue).to(exchange).with("test2").noargs();
    }
    //绑定延迟队列到普通交换机
    @Bean
    public Binding bindingDLXTestQueueToExchange(@Qualifier("DLXTestQueue") Queue queue, @Qualifier("bootExchange") Exchange exchange){
        return BindingBuilder.bind(queue).to(exchange).with("testDLX").noargs();
    }
    //绑定死信交换机和死信队列
    @Bean
    public Binding bindingDLXQueueToDLXExchange(@Qualifier("DLXQueue") Queue queue, @Qualifier("DLXExchange") Exchange exchange){
        return BindingBuilder.bind(queue).to(exchange).with("dlx").noargs();
    }
}
