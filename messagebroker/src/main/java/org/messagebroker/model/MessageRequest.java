package org.messagebroker.model;

public class MessageRequest {
    private String senderId;
    private String targetId;
    private String queueId;
    private Message message;
    
    public String getSenderId() { return senderId; }
    public void setSenderId(String senderId) { this.senderId = senderId; }
    
    public String getTargetId() { return targetId; }
    public void setTargetId(String targetId) { this.targetId = targetId; }
    
    public String getQueueId() { return queueId; }
    public void setQueueId(String queueId) { this.queueId = queueId; }
    
    public Message getMessage() { return message; }
    public void setMessage(Message message) { this.message = message; }
}
