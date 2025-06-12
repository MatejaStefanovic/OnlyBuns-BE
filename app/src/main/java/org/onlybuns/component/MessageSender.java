package org.onlybuns.component;

//import org.onlybuns.config.RabbitMQConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MessageSender {

    @Autowired
    private final RabbitTemplate rabbitTemplate;

    public MessageSender(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendMessage(String description, String creationDateTime, String username) {

        String jsonMessage = String.format("{\"description\": \"%s\", \"creationDateTime\": \"%s\", \"username\": \"%s\"}", description, creationDateTime, username);
        rabbitTemplate.convertAndSend("advertisement_fanout_exchange", "", jsonMessage);
        System.out.println("Poruka poslata: " + jsonMessage);
    }
    
}
