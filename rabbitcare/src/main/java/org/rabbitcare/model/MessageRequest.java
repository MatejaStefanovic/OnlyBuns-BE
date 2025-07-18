package org.rabbitcare.model;

public class MessageRequest {
    private String senderId;
    private String targetId;
    private String queueId;
    private Message message;

    public MessageRequest() {}

    public MessageRequest(String senderId, String targetId, String queueId, Message message) {
        this.senderId = senderId;
        this.targetId = targetId;
        this.queueId = queueId;
        this.message = message;
    }

    public String getSenderId() {
        return senderId;
    }

    public String getTargetId() {
        return targetId;
    }

    public String getQueueId() {
        return queueId;
    }

    public Message getMessage() {
        return message;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    public void setQueueId(String queueId) {
        this.queueId = queueId;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

}
