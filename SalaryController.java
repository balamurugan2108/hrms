package com.spring.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.spring.entity.EmployeeSalary;
import com.spring.service.SalaryService;

@RestController
@RequestMapping("/api/admin")
public class SalaryController {
    @Autowired
    private SalaryService service;
    @PostMapping("/save-salary")
    public EmployeeSalary saveSalary(@RequestBody EmployeeSalary salary) {
        return service.saveSalary(salary);
    }
    @GetMapping("/all-salaries")
    public List<EmployeeSalary> getAllSalaries() {
        return service.getAllSalaries();
    }
    @GetMapping("/get-salary/{employeeId}")
    public EmployeeSalary getSalary(@PathVariable String employeeId) {
        return service.getSalary(employeeId);
    }
}