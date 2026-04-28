package com.spring.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.spring.entity.Calendar;
import com.spring.service.CalendarService;

@RestController
@RequestMapping("/api/calendar")
public class CalendarController {

    @Autowired 
    private CalendarService calendarService;
    
 // CalendarController.java
    @PostMapping("/addevents")
    public ResponseEntity<?> addEvent(@RequestBody Calendar calendar) {
        try {
            Calendar savedEvent = calendarService.addEvent(calendar);
            return ResponseEntity.ok(savedEvent);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error saving holiday: " + e.getMessage());
        }
    }
    @GetMapping("/events/{empId}/{role}")
    public ResponseEntity<?> getEvents(
        @PathVariable("empId") Long empId,
        @RequestParam(value = "role", defaultValue = "USER") String role) {
        
        return ResponseEntity.ok(calendarService.getCalendarEvents(empId, role));
    }
}