package org.messagebroker.model;

public class Message {
    private long timestamp;
    // Data that is sent between apps
    // it is a JSON string 
    private String data;


    public String getData() { return data; }
    public void setData(String data) { this.data = data; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    
}
