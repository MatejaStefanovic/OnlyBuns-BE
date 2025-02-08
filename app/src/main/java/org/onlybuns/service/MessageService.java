package org.onlybuns.service;

import org.onlybuns.model.Message;
import org.onlybuns.repository.CommentRepository;
import org.onlybuns.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class MessageService {

    private final MessageRepository messageRepository;

    @Autowired
    public MessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    public Message save(Message message){
        return messageRepository.save(message);
    }

    public Set<String> getAllSendersForUser(String username){
        Set<String> senders = new HashSet<>();
       /* List<String> senders = messageRepository.findAllByReceiverUsername(username)
                .stream()
                .map(Message::getSenderUsername)
                .distinct()
                .collect(Collectors.toList());
        return senders;*/
        List<Message> msgs = messageRepository.findAllByReceiverUsername(username);
        for (Message m : msgs){
            senders.add(m.getSenderUsername());
        }
        return senders;

    }

    public Message getLastMessageSent (String sender, String receiver){
        return messageRepository.FindLastMessage(sender, receiver);
    }
}
