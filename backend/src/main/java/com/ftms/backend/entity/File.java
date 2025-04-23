package com.ftms.backend.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Column;
import org.hibernate.annotations.GenericGenerator;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
public class File {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "file_id", updatable = false, nullable = false)
    private String id;

    private String title;
    private String status;
    private String currentOfficer;
    private String courseCode;
    private String examSession;
    private String createdBy;
    private LocalDateTime timestamp;

    public File() {
        // Ensure id is initialized if not set by Hibernate
        if (this.id == null) {
            this.id = UUID.randomUUID().toString();
        }
    }

    public File(String id, String title, String status, String currentOfficer, String courseCode, String examSession, String createdBy, LocalDateTime timestamp) {
        this.id = id != null ? id : UUID.randomUUID().toString();
        this.title = title;
        this.status = status;
        this.currentOfficer = currentOfficer;
        this.courseCode = courseCode;
        this.examSession = examSession;
        this.createdBy = createdBy;
        this.timestamp = timestamp;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getCurrentOfficer() { return currentOfficer; }
    public void setCurrentOfficer(String currentOfficer) { this.currentOfficer = currentOfficer; }
    public String getCourseCode() { return courseCode; }
    public void setCourseCode(String courseCode) { this.courseCode = courseCode; }
    public String getExamSession() { return examSession; }
    public void setExamSession(String examSession) { this.examSession = examSession; }
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}