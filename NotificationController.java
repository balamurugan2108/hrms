package com.spring.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.spring.dto.NotificationResponseDTO;
import com.spring.dto.NotificationUpdateDTO;
import com.spring.entity.Notification;
import com.spring.service.NotificationService;

@RestController
@RequestMapping("/api/notification")
public class NotificationController {
	@Autowired
	private NotificationService notificationService;
	@PostMapping("/addnotification/{task_id}")
	public ResponseEntity<Notification> addNotification(
			@RequestBody Notification notification, 
			@PathVariable("task_id")Long taskId) {
		return ResponseEntity.ok(notificationService.addNotification(notification,taskId));
	}
	@GetMapping("/allnotifications")
	public ResponseEntity<List<NotificationResponseDTO>> getAllNotification(){
		return ResponseEntity.ok(notificationService.getAllNotifications());
	}
	
	@PutMapping("/updatenotification/{notificationId}")
	public ResponseEntity<?> updateNotification(
	        @PathVariable("notificationId") Long notificationId,
	        @RequestBody NotificationUpdateDTO dto) {

	    notificationService.updateNotification(notificationId, dto);
	    return ResponseEntity.ok("Notification updated successfully");
	}
	@GetMapping("/my/{userId}")
	public ResponseEntity<List<NotificationResponseDTO>> getNotificationUserById(
	        @PathVariable("userId") Long userId) {
	    return ResponseEntity.ok(notificationService.getNotificationByUserId(userId));
	}
	@GetMapping("/remainingrequests/{taskId}")
	public ResponseEntity<Integer> getRemainingRequests(@PathVariable Long taskId) {
	    int remaining = notificationService.getRemainingRequestsForTask(taskId);
	    return ResponseEntity.ok(remaining);
	}
	
}
