package com.spring.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.spring.dto.EmployeeResponseDTO;
import com.spring.dto.AddUserRequest;
import com.spring.dto.ChangePasswordRequest;
import com.spring.dto.EmployeeFilterRequest;
import com.spring.service.UserService;

import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/adduser")
    public ResponseEntity<String> addUser(@Valid @RequestBody AddUserRequest request) {
        userService.addUser(request);
        return ResponseEntity.ok("User onboarded successfully with all documents");
    }
      
    @GetMapping("/getallemployee")
    public ResponseEntity<List<EmployeeResponseDTO>> getEmployees() {
        return ResponseEntity.ok(userService.getEmployees());
    }
      
    @DeleteMapping("/deleteemployee/{id}")
    public ResponseEntity<String> deleteEmployee(@PathVariable("id") Long id) {
        userService.deleteEmployee(id);
        return ResponseEntity.ok("User and associated profiles deleted successfully");
    }
    

    @GetMapping("/employee/{id}")
    public ResponseEntity<EmployeeResponseDTO> getEmployeeById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(userService.getEmployeeById(id));
    }
    
    /**
     * Updates Employee details.
     * Can update specific fields or replace the entire dynamic documents list.
     */
    @PutMapping("/employee/{id}")
    public ResponseEntity<String> updateEmployee(
            @PathVariable("id") Long id, 
            @Valid @RequestBody AddUserRequest request) {
        userService.updateEmployee(id, request);
        return ResponseEntity.ok("Employee profile and documents updated successfully");
    }

    @PostMapping("/employees/filter")
    public ResponseEntity<List<EmployeeResponseDTO>> filterEmployees(
    		@Valid @RequestBody EmployeeFilterRequest request) {
        return ResponseEntity.ok(userService.filterEmployees(request));
    } 
    @PutMapping("/change-password/{userId}")
    public ResponseEntity<?> changePassword(
            @PathVariable Long userId,
            @RequestBody ChangePasswordRequest request) {

        try {
            userService.changePassword(userId, request.getOldPassword(), request.getNewPassword());
            return ResponseEntity.ok("Password changed successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }
}