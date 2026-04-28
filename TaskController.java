package com.spring.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spring.dto.TaskFilterRequest;
import com.spring.dto.TaskResponseDTO;
import com.spring.entity.Task;
import com.spring.repository.TaskRepository;
import com.spring.service.TaskService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin/tasks")
public class TaskController {
	@Autowired
	private TaskService service;
	@Autowired
	private TaskRepository taskRepository;
	
	@GetMapping("/alltasks")
	public ResponseEntity<List<TaskResponseDTO>> getAllTasks(){
		return ResponseEntity.ok(service.getAllTasks());
	}
	@PostMapping("/addtask")
    public ResponseEntity<TaskResponseDTO> addTask(@RequestBody Task task){
        Task savedTask = service.createTask(task);
        return ResponseEntity.ok(TaskResponseDTO.from(savedTask));
    }
    @GetMapping("/task/{id}")
    public ResponseEntity<TaskResponseDTO> getTaskById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getTaskById(id));
    }

    @PutMapping("/updatetask/{id}")
    public ResponseEntity<Task> updateTask(
            @PathVariable Long id,
            @RequestBody Task task) {

        return ResponseEntity.ok(service.updateTask(id, task));
    }

	@GetMapping("/my/{employeeId}")
	public ResponseEntity<List<TaskResponseDTO>> getMyTasks(
	        @PathVariable("employeeId") Long employeeId) {
	    return ResponseEntity.ok(service.getTasksForEmployee(employeeId));
	}
	@GetMapping("/project/{projectId}/employee/{userId}")
	public ResponseEntity<List<TaskResponseDTO>> getTasksByProjectAndEmployee(
	        @PathVariable("projectId") Long projectId, 
	        @PathVariable("userId") Long userId) {
	    List<Task> tasks = taskRepository.findTasksByProjectAndUser(projectId, userId);
	    List<TaskResponseDTO> response = tasks.stream()
	            .map(TaskResponseDTO::from) // Using your static helper method
	            .toList(); 
	    return ResponseEntity.ok(response);
	}
	@PostMapping("/filter")
	public List<TaskResponseDTO> filterTasks(
	        @RequestBody TaskFilterRequest request) {
	    return service.filterTasks(request);
	}
}
