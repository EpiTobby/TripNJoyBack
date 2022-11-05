package fr.tripnjoy.common.broker;

import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;

public class RabbitMQConfiguration
{
    public static final String TOPIC_EXCHANGE = "amq.topic";

    public RabbitTemplate rabbitTemplate()
    {
        return rabbitTemplate("127.0.0.1:5672", "guest");
    }

    public RabbitTemplate rabbitTemplate(String address, String username)
    {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setAddresses(address);
        connectionFactory.setUsername(username);
        final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
        return rabbitTemplate;
    }
}
