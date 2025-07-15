package org.messagebroker.model;

public class Message {
    private String from;
    private String to;
    private String text;
    private long timestamp;
    // Data that is sent between apps
    // it is a JSON string 
    private String data;

    public String getFrom() { return from; }
    public void setFrom(String from) { this.from = from; }

    public String getTo() { return to; }
    public void setTo(String to) { this.to = to; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    
}
