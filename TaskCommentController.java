package com.spring.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.spring.dto.TaskCommentDTO;
import com.spring.entity.TaskComment;
import com.spring.service.TaskCommentService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/taskcomment")
public class TaskCommentController {

    @Autowired
    private TaskCommentService service;
    
    @PostMapping("/{userId}/addcomment/{taskId}")
    public ResponseEntity<?> addComment(
            @PathVariable("userId") Long userId,
            @PathVariable("taskId") Long taskId,
            @Valid @RequestBody TaskComment comment) {
        return ResponseEntity.ok(service.addTaskComment(userId, taskId, comment));
    }
    
    @GetMapping("/allcomments")
    public ResponseEntity<List<TaskCommentDTO>> getAllComments(){
    	return ResponseEntity.ok(service.getAllComments());
    }

    @GetMapping("/allcomments/{taskId}")
    public List<TaskCommentDTO> getAllComment(@PathVariable("taskId") Long taskId) {
        return service.getAllComment(taskId);
    }

    // ✅ Update Comment
    @PutMapping("/updatecomment/{commentId}")
    public ResponseEntity<TaskComment> updateComment(
            @PathVariable("commentId") Long commentId,
            @Valid @RequestBody TaskComment comment) {
        return ResponseEntity.ok(service.updateComment(commentId, comment));
    }

    // ✅ Delete Comment
    @DeleteMapping("/deletecomment/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable("commentId") Long commentId) {
        service.deleteComment(commentId);
        return ResponseEntity.ok("Comment Deleted Successfully");
    }
    @GetMapping("/notifications/{employeeId}")
    public ResponseEntity<List<TaskCommentDTO>> getNotifications(
            @PathVariable("employeeId") Long employeeId) {

        return ResponseEntity.ok(service.getEmployeeNotifications(employeeId));
    }
}