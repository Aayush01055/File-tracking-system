package com.ftms.backend.controller;

import com.ftms.backend.entity.AuditLog;
import com.ftms.backend.repository.AuditLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/audit")
public class AuditController {

    @Autowired
    private AuditLogRepository auditLogRepository;

    @GetMapping("/{fileId}")
    public List<AuditLog> getAuditLogs(@PathVariable String fileId) {
        return auditLogRepository.findByFileId(fileId);
    }
}