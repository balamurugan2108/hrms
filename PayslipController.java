package com.spring.controller;

import com.spring.entity.Payslip;
import com.spring.repository.PayslipRepository;
import com.spring.service.PayslipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class PayslipController {
    @Autowired
    private PayslipService payslipService;
    @Autowired
    private PayslipRepository repo;
    // ─── 1. Save payslip metadata (called first — before PDF is attached) ─────
    @PostMapping("/generate-payslip")
    public ResponseEntity<?> generatePayslip(@RequestBody Payslip payslip) {
        try {
            Payslip saved = payslipService.saveOrUpdatePayslip(payslip);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to save payslip: " + e.getMessage());
        }
    }

    // ─── 2. Attach the PDF blob to an existing payslip record ─────────────────
    @PostMapping("/attach-payslip-pdf/{payslipId}")
    public ResponseEntity<?> attachPdf(
            @PathVariable Long payslipId,
            @RequestParam("file") MultipartFile file) {
        try {
            Payslip updated = payslipService.attachPdf(payslipId, file);
            return ResponseEntity.ok("PDF attached to payslip ID: " + updated.getId());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to attach PDF: " + e.getMessage());
        }
    }

    // ─── 3. Send payslip email (after PDF is attached) ────────────────────────
    @PostMapping("/send-payslip")
    public ResponseEntity<?> sendPayslip(@RequestBody Map<String, Object> body) {
        try {
            Long employeeId = Long.parseLong(body.get("employeeId").toString());
            String month = body.get("month").toString();
            int year = Integer.parseInt(body.get("year").toString());

            payslipService.sendPayslipEmail(employeeId, month, year);
            return ResponseEntity.ok("Payslip email sent successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to send email: " + e.getMessage());
        }
    }

    @GetMapping("/payslip/{employeeId}")
    public ResponseEntity<?> getPayslipsByEmployee(@PathVariable Long employeeId) {
        try {
            List<Payslip> slips = payslipService.getPayslipsByEmployeeId(employeeId);

            List<Map<String, Object>> result = slips.stream().map(p -> {
                String base64Pdf = p.getPayslipFile() != null
                        ? java.util.Base64.getEncoder().encodeToString(p.getPayslipFile())
                        : "";
                
                Map<String, Object> map = new HashMap<>();
                map.put("id", p.getId());
                map.put("employeeId", p.getEmployeeId()); // Needed for the template
                map.put("name", p.getName());             // Needed for the template
                map.put("month", p.getMonth());
                map.put("year", p.getYear());
                map.put("paidDays", p.getPaidDays());
                map.put("lopDays", p.getLopDays());
                
                // Individual Components for the Popup Table
                map.put("basic", p.getBasic() != null ? p.getBasic() : 0.0);
                map.put("hra", p.getHra() != null ? p.getHra() : 0.0);
                map.put("allowance", p.getAllowance() != null ? p.getAllowance() : 0.0);
                map.put("bonus", p.getBonus() != null ? p.getBonus() : 0.0);
                map.put("otherBenefits", p.getOtherBenefits() != null ? p.getOtherBenefits() : 0.0);
                map.put("pf", p.getPf() != null ? p.getPf() : 0.0);
                
                // Totals
                map.put("totalEarnings", p.getTotalEarnings() != null ? p.getTotalEarnings() : 0.0);
                map.put("totalDeductions", p.getTotalDeductions() != null ? p.getTotalDeductions() : 0.0);
                map.put("netPay", p.getNetPay() != null ? p.getNetPay() : 0.0);
                
                map.put("payslipFile", base64Pdf);

                return map;
            }).toList();

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching payslips: " + e.getMessage());
        }
    }

    // ─── 5. Get all payslips (admin view — AllPayslips page) ──────────────────
    @GetMapping("/all-payslips")
    public ResponseEntity<?> getAllPayslips() {
        try {
            List<Payslip> slips = payslipService.getAllPayslips();
            List<Map<String, Object>> result = slips.stream().map(p -> {
                String base64Pdf = p.getPayslipFile() != null
                        ? java.util.Base64.getEncoder().encodeToString(p.getPayslipFile())
                        : null;
                return Map.<String, Object>of(
                        "id", p.getId(),
                        "employeeId", p.getEmployeeId(),
                        "name", p.getName() != null ? p.getName() : "",
                        "email", p.getEmail() != null ? p.getEmail() : "",
                        "month", p.getMonth(),
                        "year", p.getYear(),
                        "paidDays", p.getPaidDays(),
                        "lopDays", p.getLopDays(),
                        "netPay", p.getNetPay() != null ? p.getNetPay() : 0.0,
                        "payslipFile", base64Pdf != null ? base64Pdf : ""
                );
            }).toList();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + e.getMessage());
        }
    }
    @GetMapping("/payslips")
    public List<Payslip> allPayslip(){
    	return repo.findAll();
    }
}