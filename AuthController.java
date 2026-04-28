package com.spring.controller;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import com.spring.dto.LoginRequest;
import com.spring.dto.LoginResponse;
import com.spring.entity.UserStatus;
import com.spring.entity.Users;
import com.spring.exception.ErrorResponse;
import com.spring.repository.UserRepository;
import com.spring.security.JwtUtil;
import com.spring.service.MailService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
	private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired 
    private MailService mailService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
    	 
        Users user = userRepository.findByEmail(request.getEmail());
        if (user == null) {
            logger.debug("Login failed: user not found for email={}", request.getEmail());
            return ResponseEntity.status(401).body("Invalid credentials");
        }
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            logger.debug("Login failed: password mismatch for email={}", request.getEmail());
            return ResponseEntity.status(401).body("Invalid credentials");
        }
        if (user.getStatus() == UserStatus.INACTIVE) {
            logger.debug("Login blocked: inactive user email={}", request.getEmail());
            return ResponseEntity.status(403).body("Account deactivated");
        }
        String token = jwtUtil.generateToken(user.getEmail(), user.getRole());
        String name = "";
        if (user.getEmployee() != null) {
            String firstName = user.getEmployee().getFirst_Name();
            String lastName = user.getEmployee().getLast_Name();
            if (firstName != null || lastName != null) {
                name = (firstName != null ? firstName : "") + " " +
                       (lastName != null ? lastName : "");
            }
        } else {
            name = user.getName(); 
        }
        boolean mustChangePassword = user.isMustChangePassword();
        logger.debug("Login successful for email={}", request.getEmail());
        return ResponseEntity.ok(
                new LoginResponse(user.getEmail(), user.getRole(), token , user.getId(),name,user.getStatus(),mustChangePassword)
            );
    }
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> request) {

        String email = request.get("email");

        Users user = userRepository.findByEmail(email);
        if (user == null) {
            return ResponseEntity.ok("If email exists, reset link sent"); // 🔒 prevent email enumeration
        }

        String token = UUID.randomUUID().toString();
        user.setResetToken(token);
        user.setTokenExpiry(LocalDateTime.now().plusMinutes(15));

        userRepository.save(user);

        String resetLink = "http://localhost:5173/reset-password/" + token;

        // Use your existing mail service
        mailService.sendEmail(
            email,
            "Password Reset",
            "Click here to reset your password: " + resetLink
        );

        return ResponseEntity.ok("Reset link sent");
    }
    @PostMapping("/reset-password/{token}")
    public ResponseEntity<?> resetPassword(
            @PathVariable String token,
            @RequestBody Map<String, String> request) {

        Users user = userRepository.findByResetToken(token);

        if (user == null) {
            return ResponseEntity.status(400).body("Invalid token");
        }

        if (user.getTokenExpiry().isBefore(LocalDateTime.now())) {
            return ResponseEntity.status(400).body("Token expired");
        }

        user.setPassword(passwordEncoder.encode(request.get("password")));
        user.setResetToken(null); // ✅ one-time use
        user.setTokenExpiry(null);

        userRepository.save(user);

        return ResponseEntity.ok("Password updated successfully");
    }
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex) {

        ErrorResponse error = new ErrorResponse(
                "Access denied",
                HttpStatus.FORBIDDEN.value()
        );

        return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
    }
}
