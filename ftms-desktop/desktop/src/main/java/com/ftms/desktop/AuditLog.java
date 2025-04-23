package com.ftms.desktop;

import java.time.LocalDateTime;

public class AuditLog {
    private String id;
    private String action;
    private String userId;
    private String fileId;
    private LocalDateTime timestamp;

    public AuditLog() {}

    public AuditLog(String id, String action, String userId, String fileId, LocalDateTime timestamp) {
        this.id = id;
        this.action = action;
        this.userId = userId;
        this.fileId = fileId;
        this.timestamp = timestamp;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getFileId() { return fileId; }
    public void setFileId(String fileId) { this.fileId = fileId; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}