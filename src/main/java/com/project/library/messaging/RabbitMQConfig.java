package com.project.library.messaging;

import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Declarables;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    private final CachingConnectionFactory cachingConnectionFactory;
    public RabbitMQConfig(CachingConnectionFactory cachingConnectionFactory) {
        this.cachingConnectionFactory = cachingConnectionFactory;
    }
    public static final String EXCHANGE_AVERAGE_DIRECT = "x.average.direct";
    public static final String ROUTING_KEY_AVERAGE_FIND = "average.return.book";

    @Bean
    public Queue queueAverageReturn() {
        return new Queue("q.average.bookReturn");
    }
    @Bean
    public DirectExchange exchangeAverageDirect() {
        return new DirectExchange(EXCHANGE_AVERAGE_DIRECT);
    }
    @Bean
    public Declarables directExchangeBindings(
            DirectExchange exchangeAverageDirect,
            Queue queueAverageReturn) {
        return new Declarables(
                BindingBuilder.bind(queueAverageReturn).to(exchangeAverageDirect).with(ROUTING_KEY_AVERAGE_FIND)
        );
    }
    @Bean
    public Jackson2JsonMessageConverter converter() {
        return new Jackson2JsonMessageConverter();
    }
    @Bean
    public RabbitTemplate rabbitTemplate(Jackson2JsonMessageConverter converter) {
        RabbitTemplate template = new RabbitTemplate(cachingConnectionFactory);
        template.setMessageConverter(converter);
        return template;
    }
}