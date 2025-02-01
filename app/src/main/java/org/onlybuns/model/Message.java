package org.onlybuns.model;

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
    private String SenderUsername;
    @NotNull
    private String receiverUsername;
    @NotNull
    private String content;

    private LocalDateTime time;

    private boolean isRead;

    public Message(int id, String receiverUsername, String senderUsername, String content, LocalDateTime time, boolean isRead) {
        this.id = id;
        this.receiverUsername = receiverUsername;
        SenderUsername = senderUsername;
        this.content = content;
        this.time = time;
        this.isRead = isRead;
    }

    public @NotNull String getSenderUsername() {
        return SenderUsername;
    }

    public void setSenderUsername(@NotNull String senderUsername) {
        SenderUsername = senderUsername;
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
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }
}
