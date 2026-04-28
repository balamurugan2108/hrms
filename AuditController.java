package com.spring.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.spring.entity.AuditLog;
import com.spring.repository.AuditLogRepository;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AuditController {
    @Autowired
    private AuditLogRepository repo;
    @GetMapping("/audit-logs")
    public List<AuditLog> getAllLogs() {
        return repo.findAll();
    }
}