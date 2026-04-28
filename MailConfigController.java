package com.spring.controller;
 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
 
import com.spring.entity.MailConfig;
import com.spring.repository.MailConfigRepository;
 
@RestController
@RequestMapping("/api/admin/mail-config")
@PreAuthorize("hasRole('ADMIN')")
public class MailConfigController {
 
    @Autowired
    private MailConfigRepository repo;
 
    /** Create — called when no record exists yet */
    @PostMapping
    public ResponseEntity<MailConfig> saveConfig(@RequestBody MailConfig config) {
        MailConfig saved = repo.save(config);
        return ResponseEntity.ok(saved);          // return entity so frontend gets the id
    }
 
    /** Read — always returns the single/first config row */
    @GetMapping("/get")
    public ResponseEntity<?> get() {
        return repo.findAll()
                   .stream()
                   .findFirst()
                   .<ResponseEntity<?>>map(ResponseEntity::ok)
                   .orElse(ResponseEntity.noContent().build()); // 204 when nothing saved yet
    }
 
    /** Update — called when a record already exists */
    @PutMapping("/edit/{id}")
    public ResponseEntity<MailConfig> editMail(
            @PathVariable Long id,
            @RequestBody MailConfig mail) {
 
        MailConfig existing = repo.findById(id).orElseThrow();
        existing.setUsername(mail.getUsername());
        existing.setHost(mail.getHost());
        existing.setPort(mail.getPort());
        // Only overwrite password if a new one was actually sent
        if (mail.getPassword() != null && !mail.getPassword().isBlank()) {
            existing.setPassword(mail.getPassword());
        }
        return ResponseEntity.ok(repo.save(existing));
    }
}