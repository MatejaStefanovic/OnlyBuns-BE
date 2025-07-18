package org.messagebroker.model;

import java.util.List;

public class MessageResponse {
    private String status;
    private List<Message> messages;

    public MessageResponse(String status, List<Message> messages) {
        this.status = status;
        this.messages = messages;
    }
 
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }  

}
