package org.onlybuns.controller;

import org.onlybuns.component.MessageSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/messages")
public class MessageController {

    private final MessageSender messageSender;

    @Autowired
    public MessageController(MessageSender messageSender) {
        this.messageSender = messageSender;
    }

    @PostMapping
    public String sendMessage(@RequestParam String description, @RequestParam String creationDateTime) {
        messageSender.sendMessage(description, creationDateTime);
        return "Poruka uspešno poslata!";
    }



}
