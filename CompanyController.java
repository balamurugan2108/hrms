package com.spring.controller;
 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.spring.service.CompanyService;
 
@RestController
@RequestMapping("/api/admin/company")
@PreAuthorize("hasRole('ADMIN')")
public class CompanyController {
 
    @Autowired
    private CompanyService service;
 
    /** Create */
    @PostMapping("/save")
    public ResponseEntity<?> saveCompany(
            @RequestParam String name,
            @RequestParam String address,
            @RequestParam(required = false) MultipartFile logo ,  // logo optional on edit
            @RequestParam(required = false) MultipartFile icon 
    ) throws Exception {
        return ResponseEntity.ok(service.saveCompany(name, address, logo,icon));
    }
 
    /** Read */
    @GetMapping("/get")
    public ResponseEntity<?> getCompany() {
        return ResponseEntity.ok(service.getCompany());
    }
 
    /** Update — id in path, multipart body */
    @PutMapping("/edit/{id}")
    public ResponseEntity<?> editCompany(
            @PathVariable Long id,
            @RequestParam String name,
            @RequestParam String address,
            @RequestParam(required = false) byte[] logo,
            @RequestParam(required = false) byte[] icon
    ) throws Exception {
        return ResponseEntity.ok(service.editCompany(id, name, address, logo,icon));
    }
}