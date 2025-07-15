package org.messagebroker.model;

public class MessageRequest {
    private String appId;
    private String queueId;
    private Message message;

    public String getAppId() { return appId; }
    public void setAppId(String appId) { this.appId = appId; }

    public String getQueueId() { return queueId; }
    public void setQueueId(String queueId) { this.queueId = queueId; }

    public Message getMessage() { return message; }
    public void setMessage(Message message) { this.message = message; }
}

