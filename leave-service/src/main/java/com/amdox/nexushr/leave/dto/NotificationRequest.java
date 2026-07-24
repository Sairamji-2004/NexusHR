package com.amdox.nexushr.leave.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public class NotificationRequest {

    private UUID employeeId;
    private String title;
    private String message;
    private String type;
    private boolean isRead;
    private LocalDateTime createdAt;

    public NotificationRequest() {
    }

    public NotificationRequest(
            UUID employeeId,
            String title,
            String message,
            String type) {

        this.employeeId = employeeId;
        this.title = title;
        this.message = message;
        this.type = type;

        this.isRead = false;
        this.createdAt = LocalDateTime.now();
    }

    public UUID getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(UUID employeeId) {
        this.employeeId = employeeId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}