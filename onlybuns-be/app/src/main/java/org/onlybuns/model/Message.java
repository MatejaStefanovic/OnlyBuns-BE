package org.onlybuns.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Entity
@Table(name = "messages")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotNull
    @JsonProperty("senderUsername")
    private String senderUsername;
    @NotNull
    private String receiverUsername;
    @NotNull
    private String content;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Column(name = "date_time", nullable = false)
    private LocalDateTime dateTime;

    private boolean isRead;

    public Message(){

    }
    public Message(int id, String receiverUsername, String senderUsername, String content, LocalDateTime dateTime, boolean isRead) {
        this.id = id;
        this.receiverUsername = receiverUsername;
        this.senderUsername = senderUsername;
        this.content = content;
        this.dateTime = dateTime;
        this.isRead = isRead;
    }

    public @NotNull String getSenderUsername() {
        return senderUsername;
    }

    public void setSenderUsername(@NotNull String senderUsername) {
        this.senderUsername = senderUsername;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public @NotNull String getReceiverUsername() {
        return receiverUsername;
    }

    public void setReceiverUsername(@NotNull String receiverUsername) {
        this.receiverUsername = receiverUsername;
    }

    public @NotNull String getContent() {
        return content;
    }

    public void setContent(@NotNull String content) {
        this.content = content;
    }

    public LocalDateTime getTime() {
        return dateTime;
    }

    public void setTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        this.isRead = read;
    }
}
