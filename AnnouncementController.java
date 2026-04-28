package com.spring.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.spring.entity.Announcement;
import com.spring.repository.AnnouncementRepository;
import com.spring.repository.EmployeeRepository;
import com.spring.service.MailService;

@RestController
@RequestMapping("/api/announcements")
public class AnnouncementController {
    @Autowired
    private AnnouncementRepository announcementRepo;
    @Autowired
    private EmployeeRepository employeeRepo;
    @Autowired
    private MailService mailService;
    @PostMapping
    public Announcement createAnnouncement(
            @RequestParam String subject,
            @RequestParam String content,
            @RequestParam(required = false) MultipartFile file
    ) throws Exception {
        Announcement announcement = new Announcement();
        announcement.setSubject(subject);
        announcement.setContent(content);
        if (file != null && !file.isEmpty()) {
            announcement.setAttachment(file.getBytes());
            announcement.setFileName(file.getOriginalFilename());
            announcement.setFileType(file.getContentType());
        }
        announcement.setCreatedAt(LocalDateTime.now());
        Announcement saved = announcementRepo.save(announcement);
        List<String> emails = employeeRepo.findAll()
                .stream()
                .map(emp -> emp.getUser().getEmail())
                .collect(Collectors.toList());

        mailService.sendAnnouncementMail(saved, emails);
        return saved;
    }
    @GetMapping
    public List<Announcement> getAll() {
        return announcementRepo.findAll();
    }
    @GetMapping("/{id}/file")
    public ResponseEntity<byte[]> downloadFile(@PathVariable Long id) {

        Announcement announcement = announcementRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Announcement not found"));

        String fileName = announcement.getFileName() != null
                ? announcement.getFileName()
                : "announcement.pdf";

        String fileType = announcement.getFileType() != null
                ? announcement.getFileType()
                : "application/pdf";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"" + fileName + "\"")
                .contentType(MediaType.parseMediaType(fileType))
                .body(announcement.getAttachment());
    }
    @PutMapping("/{id}")
    public Announcement updateAnnouncement(
            @PathVariable Long id,
            @RequestParam String subject,
            @RequestParam String content,
            @RequestParam(required = false) MultipartFile file
    ) throws Exception {
        Announcement a = announcementRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Announcement not found"));
        a.setSubject(subject);
        a.setContent(content);
        a.setUpdatedAt(LocalDateTime.now());

        if (file != null && !file.isEmpty()) {
            a.setAttachment(file.getBytes());
            a.setFileName(file.getOriginalFilename());
            a.setFileType(file.getContentType());
        }

        return announcementRepo.save(a);
    }
    @DeleteMapping("/{id}")
    public String deleteAnnouncement(@PathVariable Long id) {

        announcementRepo.deleteById(id);

        return "Announcement deleted successfully";
    }
}