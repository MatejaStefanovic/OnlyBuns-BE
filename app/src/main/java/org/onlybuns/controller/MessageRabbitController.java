package org.onlybuns.controller;

import org.onlybuns.component.MessageRabbitSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/messages")
public class MessageRabbitController {

    private final MessageRabbitSender messageRabbitSender;

    @Autowired
    public MessageRabbitController(MessageRabbitSender messageRabbitSender) {
        this.messageRabbitSender = messageRabbitSender;
    }

    @PostMapping
    public String sendMessage(@RequestParam String description, @RequestParam String creationDateTime, @RequestParam String username) {
        messageRabbitSender.sendMessage(description, creationDateTime, username);
        return "Poruka uspešno poslata!";
    }



}
