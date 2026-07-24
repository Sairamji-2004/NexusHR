package com.amdox.nexushr.PerformanceController;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.amdox.nexushr.PerformanceService.PerformanceService;
import com.amdox.nexushr.entity.Performance;

@RestController
@RequestMapping("/api/performance")
@CrossOrigin("*")
public class PerformanceController {

    private final PerformanceService service;

    public PerformanceController(PerformanceService service) {
        this.service = service;
    }

    // Create Performance Review
    @PostMapping
    public ResponseEntity<Performance> create(
            @RequestBody Performance performance) {

        System.out.println("========== PERFORMANCE REVIEW ==========");
        System.out.println("Employee : " + performance.getEmployeeName());
        System.out.println("Reviewer : " + performance.getReviewer());
        System.out.println("Rating   : " + performance.getRating());

        Performance saved = service.save(performance);

        return ResponseEntity.ok(saved);
    }

    // Get All Reviews
    @GetMapping
    public ResponseEntity<List<Performance>> getAll() {

        return ResponseEntity.ok(service.getAll());
    }

    // Get Reviews By Employee
    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<Performance>> getByEmployee(
            @PathVariable UUID employeeId) {

        return ResponseEntity.ok(service.getByEmployee(employeeId));
    }

    // Get Single Review
    @GetMapping("/{id}")
    public ResponseEntity<Performance> getById(
            @PathVariable UUID id) {

        Performance performance = service.getById(id);

        if (performance == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(performance);
    }

    // Update Review
    @PutMapping("/{id}")
    public ResponseEntity<Performance> update(
            @PathVariable UUID id,
            @RequestBody Performance performance) {

        Performance updated = service.update(id, performance);

        if (updated == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(updated);
    }

    // Delete Review
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(
            @PathVariable UUID id) {

        service.delete(id);

        return ResponseEntity.ok("Performance review deleted successfully");
    }
}