package com.zl.blog.config;

import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author shkstart
 * @create 2022-08-28 21:13
 */
@Configuration
public class Mqconfig {


    @Bean
    public Queue queue(){
        return new Queue("blog-update-article");    // 声明 消息队列
    }

    @Bean
    public BindingBuilder.DestinationConfigurer binding(Queue queue){
        return BindingBuilder.bind(queue);
    }

}
