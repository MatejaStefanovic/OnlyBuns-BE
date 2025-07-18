package org.rabbitcare.model;

public class Message {
    private long timestamp;
    private String data;

    public Message() {}

    public Message(long timestamp, String data) {
        this.timestamp = timestamp;
        this.data = data;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getData() {
        return data;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public void setData(String data) {
        this.data = data;
    }
}
