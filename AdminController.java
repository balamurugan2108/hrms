package com.spring.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import com.spring.entity.AdminCC;
import com.spring.service.AdminService;
@RestController
@RequestMapping("/api/admin")
public class AdminController {
	private String name;
    @Autowired
    private AdminService service;
    @PostMapping("/add")
    public AdminCC addAdmin(@RequestBody AdminCC admin) { // Added @RequestBody
        return service.addAdmin(admin);
    }
    private String name;
    @GetMapping("/getAll")
    public List<AdminCC> getAllAdmin(){
        return service.getAll();
    }

    @DeleteMapping("/delete/{id}") // Added /{id}
    public void delete(@PathVariable Long id) {
        service.deleteAdmin(id);
    }
}
