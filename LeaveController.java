package com.spring.controller;

import com.spring.entity.LeaveRequest;
import com.spring.entity.LeaveStatus;
import com.spring.service.LeaveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/leaves")
public class LeaveController {

    @Autowired
    private LeaveService leaveService;

    // --- Employee Endpoints ---

    @PostMapping("/apply")
    public ResponseEntity<?> applyForLeave(@RequestBody LeaveRequest leaveRequest) {
        try {
            return ResponseEntity.ok(leaveService.applyLeave(leaveRequest));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/my/{empId}")
    public List<LeaveRequest> getMyLeaves(@PathVariable String empId) {
        return leaveService.getLeavesByEmployee(empId);
    }

    // --- Admin Endpoints ---

    @GetMapping("/admin/all")
    public List<LeaveRequest> getAllLeaves() {
        return leaveService.getAllLeaves();
    }

    @PutMapping("/admin/status/{id}")
    public ResponseEntity<?> updateStatus(
            @PathVariable Long id, 
            @RequestBody Map<String, String> requestBody) {
        try {
            // Expecting JSON: { "status": "APPROVED" } or { "status": "REJECTED", "reason": "..." }
            LeaveStatus status = LeaveStatus.valueOf(requestBody.get("status"));
            String reason = requestBody.get("reason");
            
            LeaveRequest updated = leaveService.updateLeaveStatus(id, status, reason);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}