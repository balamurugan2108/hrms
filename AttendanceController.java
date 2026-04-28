package com.spring.controller;

import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.spring.dto.AdminTimesheetDTO;
import com.spring.dto.AttendanceRequest;
import com.spring.dto.TimesheetDTO;
import com.spring.entity.Employee;
import com.spring.service.AttendanceService;

@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {
	hrms private;
    @Autowired private AttendanceService service;
    @PostMapping("/punch-in/{empId}")
    public ResponseEntity<String> punchIn(
            @PathVariable Long empId,
            @RequestParam Long taskId) {

        return ResponseEntity.ok(
            service.processPunchIn(empId, taskId)
        );
    }
    @PostMapping("/punch-out/{empId}")
    public ResponseEntity<String> punchOut(@PathVariable Long empId) {
        return ResponseEntity.ok(service.processPunchOut(empId));
    }

    @GetMapping("/daily-log/{empId}")
    public ResponseEntity<Map<String, Object>> getDailyLog(@PathVariable Long empId) {
        return ResponseEntity.ok(service.getDailyLog(empId));
    }

    @GetMapping("/timesheet/{empId}")
    public ResponseEntity<List<TimesheetDTO>> getTimesheet(@PathVariable Long empId) {
        return ResponseEntity.ok(service.getTimesheet(empId));
    }
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/timesheet")
    public ResponseEntity<List<AdminTimesheetDTO>> getAllEmployeeTimesheet() {
        return ResponseEntity.ok(service.getAllEmployeeTimesheet());
    }
    @PostMapping("/admin/set-office-time/{start}/{end}")
    public ResponseEntity<String> setOfficeTime(
            @PathVariable String start,
            @PathVariable String end) {

        return ResponseEntity.ok(
                service.setOfficeTime(
                        LocalTime.parse(start),
                        LocalTime.parse(end)
                )
        );
    }
}
