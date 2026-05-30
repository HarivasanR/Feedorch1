package com.feedorch1.feedorch1.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.MessageConverter;

@Configuration
public class RabbitMQConfig {
//post fanout infra strings
    public static final String EXCHANGE_NAME = "feed.fanout.exchange";
    public static final String QUEUE_NAME = "feed.fanout.queue";
    public static final String ROUTING_KEY = "feed.fanout.routingKey";

    //telemetry tracking constants
    public static final String INTERACTION_EXCHANGE = "user.interaction.exchange";
    public static final String INTERACTION_QUEUE = "user.interaction.queue";
    public static final String INTERACTION_ROUTING_KEY = "user.interaction.routingKey";
    
@Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    // Explicitly bind the JSON converter to your outgoing RabbitTemplate pipeline
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }
    @Bean
    public TopicExchange fanoutExchange() {
        return new TopicExchange(EXCHANGE_NAME);
    } //create the exchange

    @Bean
    public Queue fanoutQueue() {
        return new Queue(QUEUE_NAME, true); // durable queue
    } //create the queue

    @Bean
    public Binding binding(Queue fanoutQueue, TopicExchange fanoutExchange) {
        return BindingBuilder.bind(fanoutQueue).to(fanoutExchange).with(ROUTING_KEY);
    } //bind the queue to the exchange with the routing key

    @Bean
    public TopicExchange interactionExchange() {
        return new TopicExchange(INTERACTION_EXCHANGE);
    }
    public Queue interactionQueue() {
        return new Queue(INTERACTION_QUEUE, true);
    }
    public Binding bindInteractionQueue(Queue interactionQueue, TopicExchange interactionExchange) {
        return BindingBuilder.bind(interactionQueue).to(interactionExchange).with(INTERACTION_ROUTING_KEY);
    }
}
