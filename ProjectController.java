package com.spring.controller;

import org.springframework.http.MediaType;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.spring.dto.*;
import com.spring.entity.Project;
import com.spring.entity.ProjectStatus;
import com.spring.repository.ProjectRepository;
import com.spring.service.ProjectService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("api/admin/projects")
public class ProjectController {
	@Autowired 
	private ProjectService service;
	@Autowired
	private ProjectRepository repository;
	@PostMapping(value="/addproject",
	        consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<?> addProject(@Valid
	        @RequestParam String projectName,
	        @RequestParam(required=false) String description,
	        @RequestParam(required=false) String startDate,
	        @RequestParam(required=false) String endDate,
	        @RequestParam String status,
	        @RequestParam(required=false) List<String> skills,
	        @RequestParam(required=false) List<Long> managerIds,
	        @RequestParam(required=false) List<Long> memberIds,
	        @RequestParam(required=false) MultipartFile reportFile,
	        @RequestParam(required=false) MultipartFile documentFile
	) throws Exception {

	    Project project = new Project();
	    project.setProjectName(projectName);
	    project.setDecsription(description);
	    project.setStatus(ProjectStatus.valueOf(status));

	    if (startDate != null && !startDate.isEmpty())
	        project.setStartDate(LocalDate.parse(startDate));

	    if (endDate != null && !endDate.isEmpty())
	        project.setEndDate(LocalDate.parse(endDate));

	    // Skills
	    if (skills != null)
	        project.setSkills(skills);

	    return ResponseEntity.ok(
	            service.addProject(
	                    project,
	                    managerIds,
	                    memberIds,
	                    reportFile,
	                    documentFile
	            )
	    );
	}
	
	@GetMapping("/allprojects")
	public ResponseEntity<List<ProjectResponseDTO>> getAllProjects(){
		return ResponseEntity.ok(service.getAllProjects());
	}
	
	@GetMapping("/project/{id}")
	public ResponseEntity<ProjectResponseDTO> getProject(@PathVariable("id") Long id) {
	    return ResponseEntity.ok(service.getProject(id));
	}

	
	@PutMapping(value = "/updateproject/{id}", 
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
public ResponseEntity<?> updateProject(
        @PathVariable Long id,
        @RequestParam String projectName,
        @RequestParam String description,
        @RequestParam String startDate,
        @RequestParam String endDate,
        @RequestParam String status,
        @RequestParam(required = false) List<String> skills,
        @RequestParam(required = false) List<Long> managerIds,
        @RequestParam(required = false) List<Long> memberIds,
        @RequestParam(required = false) MultipartFile reportFile,
        @RequestParam(required = false) MultipartFile documentFile,
        @RequestParam(required = false) List<String> reviewNames,
        @RequestParam(required = false) List<String> reviewDates,
        @RequestParam(required = false) List<MultipartFile> reviewFiles,
        @RequestParam(required = false, defaultValue = "false") Boolean clearReviews 
) throws Exception {

    // convert dates
    LocalDate sDate = LocalDate.parse(startDate);
    LocalDate eDate = LocalDate.parse(endDate);
    return ResponseEntity.ok(
            service.updateProject(
                    id,
                    projectName,
                    description,
                    sDate,
                    eDate,
                    status,
                    skills,
                    managerIds,
                    memberIds,
                    reportFile,
                    documentFile,
                    reviewNames,
                    reviewDates,
                    reviewFiles,
                    clearReviews
            )
    );
}
	
	
	 @PostMapping("/filter")
	    public List<ProjectResponseDTO> filterProjects(@Valid @RequestBody ProjectFilterRequest request) {
	        return service.filterProjects(request);
	    }
	 @GetMapping("/download/report/{id}")
	 public ResponseEntity<byte[]> downloadReport(@PathVariable Long id) {

	     Project project = repository.findById(id)
	             .orElseThrow(() -> new RuntimeException("Project not found"));
	     if (project.getReportFile() == null || project.getReportFile().length == 0) {
	         return ResponseEntity.status(404).body(null);
	     }
	     return ResponseEntity.ok()
	             .header("Content-Disposition",
	                     "attachment; filename=\"" + project.getReportFileName() + "\"")
	             .contentType(MediaType.APPLICATION_OCTET_STREAM)
	             .body(project.getReportFile());
	 }
	// Create Review
	 @PostMapping(value = "/{id}/reviews", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	 public ResponseEntity<?> addReview(
	         @PathVariable Long id,
	         @RequestParam String reviewName,
	         @RequestParam String reviewDate,
	         @RequestParam(required = false) MultipartFile reviewFile
	 ) throws Exception {

	     return ResponseEntity.ok(
	             service.addReview(
	                     id,
	                     reviewName,
	                     LocalDate.parse(reviewDate),
	                     reviewFile
	             )
	     );
	 }

	// Get All Reviews for a Project
	 @GetMapping("/{id}/reviews")
	 public ResponseEntity<List<ReviewDTO>> getReviews(@PathVariable Long id) {
	     return ResponseEntity.ok(service.getReviewsByProjectId(id));
	 }
 }