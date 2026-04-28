package com.spring.controller;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.spring.dto.WorksheetRequest;
import com.spring.entity.OfficeTimeConfig;
import com.spring.entity.Task;
import com.spring.entity.Users;
import com.spring.entity.Worksheet;
import com.spring.entity.WorksheetEntry;
import com.spring.repository.OfficeTimeConfigRepository;
import com.spring.repository.TaskRepository;
import com.spring.repository.UserRepository;
import com.spring.repository.WorksheetRepository;


@RestController
@RequestMapping("/api/worksheet")
public class WorksheetController {
    @Autowired
    private WorksheetRepository worksheetRepository;
    @Autowired
    private TaskRepository repo;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private OfficeTimeConfigRepository officeTimeConfigRepository;
    @PostMapping("/save")
    public ResponseEntity<?> save(@RequestBody WorksheetRequest request) {
        // Fetch office time config
        OfficeTimeConfig config = officeTimeConfigRepository.findById(1L).orElse(null);
        if (config == null) {
            return ResponseEntity.badRequest().body("Office time configuration not found");
        }

        Optional<Users> optionalUser = userRepository.findById(request.getEmployeeId());
        if (optionalUser.isEmpty()) {
            return ResponseEntity.badRequest().body("Employee not found");
        }
        Users user = optionalUser.get();

        Worksheet worksheet = new Worksheet();
        worksheet.setEmployeeName(user.getName());
        worksheet.setEmployeeId(request.getEmployeeId());
        worksheet.setDate(request.getDate());

        List<WorksheetEntry> entryList = request.getEntries();
        if (entryList != null && !entryList.isEmpty()) {
            for (WorksheetEntry e : entryList) {
                // Validate entry times
            	if (e.getStartTime() == null || e.getEndTime() == null) {
            	    return ResponseEntity.badRequest().body(
            	        "Task " + e.getTaskId() + " must have startTime and endTime"
            	    );
            	}
            	LocalTime start = LocalTime.parse(e.getStartTime());
            	LocalTime end = LocalTime.parse(e.getEndTime());
            	if (start.isBefore(config.getStartTime()) ||
            	    end.isAfter(config.getEndTime()) ||
            	    end.isBefore(start)) {
            	    return ResponseEntity.badRequest().body(
            	        "Task " + e.getTaskId() + " times must be within office hours " +
            	        config.getStartTime() + " - " + config.getEndTime()
            	    );
            	}

            	// optionally set back the parsed LocalTime in the entry if entity supports it

                e.setWorksheet(worksheet);
                Task task = repo.findById(e.getTaskId()).orElse(null);
                if (task != null) {
                    task.setStatus(e.getStatus());
                    repo.save(task);
                }
            }
            worksheet.setEntries(entryList);
        }

        return ResponseEntity.ok(worksheetRepository.save(worksheet));
    }

    @GetMapping("/employee/{empId}")
    public List<Worksheet> getHistory(@PathVariable Long empId) {
        System.out.println("EMP ID RECEIVED: " + empId);
        return worksheetRepository.findByEmployeeIdOrderByDateDesc(empId);
    }
    
    @GetMapping("/all")
    public List<Worksheet> getAll(){
    	return worksheetRepository.findAll();
    }
}