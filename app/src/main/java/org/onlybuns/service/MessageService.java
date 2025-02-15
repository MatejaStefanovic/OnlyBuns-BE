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
        List<Message> msgs = messageRepository.findAllChats(username);
        for (Message m : msgs){
            if(!m.getSenderUsername().equals( username)){
            senders.add(m.getSenderUsername());}
      else {
                if (m.getReceiverUsername() != null && !m.getReceiverUsername().matches("\\d+")) {
                    senders.add(m.getReceiverUsername());
                }
            }

            }
        return senders;

    }

    public Message getLastMessageSent (String sender, String receiver){
        return messageRepository.FindLastMessage(sender, receiver);
    }

    public List<Message> getPreviousMessages ( String sender, String receiver){
        return messageRepository.FindPreviousMEssages(sender,receiver);
    }

    public List<Message> getAllByReceiverUsername(String receiverUsername){
        return messageRepository.findAllByReceiverUsername(receiverUsername);
    }
    public void markRead ( String sender, String receiver) {
        List<Message> mess = messageRepository.findAllReceivedFromUser(sender, receiver);
        for (Message m : mess) {
            m.setRead(true);
            messageRepository.save(m);
        }

    }
}
