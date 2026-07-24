package com.amdox.nexushr.PerformanceService;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.amdox.nexushr.PerformanceRepository.PerformanceRepository;
import com.amdox.nexushr.client.NotificationClient;
import com.amdox.nexushr.dto.NotificationRequest;
import com.amdox.nexushr.entity.Performance;

@Service
public class PerformanceService {

    private final PerformanceRepository repository;
    private final NotificationClient notificationClient;

    public PerformanceService(
            PerformanceRepository repository,
            NotificationClient notificationClient) {

        this.repository = repository;
        this.notificationClient = notificationClient;
    }

    // Create Performance Review
    public Performance save(Performance performance) {
    	System.out.println(">>>>>>>> PerformanceService.save() CALLED <<<<<<<<");

        Performance saved = repository.save(performance);

        NotificationRequest notification = new NotificationRequest();

        notification.setEmployeeId(saved.getEmployeeId());
        notification.setTitle("Performance Review Submitted");
        notification.setMessage(
                "Your performance review has been submitted successfully."
        );
        notification.setType("SUCCESS");

        notificationClient.send(notification);

        return saved;
    }

    // Get All Reviews
    public List<Performance> getAll() {
        return repository.findAll();
    }

    // Get Reviews By Employee
    public List<Performance> getByEmployee(UUID employeeId) {
        return repository.findByEmployeeId(employeeId);
    }

    // Get Review By Id
    public Performance getById(UUID id) {
        return repository.findById(id).orElse(null);
    }

    // Update Review
    public Performance update(UUID id, Performance performance) {

        Performance existing = repository.findById(id).orElse(null);

        if (existing == null) {
            return null;
        }

        existing.setEmployeeId(performance.getEmployeeId());
        existing.setEmployeeName(performance.getEmployeeName());
        existing.setDepartment(performance.getDepartment());
        existing.setRating(performance.getRating());
        existing.setFeedback(performance.getFeedback());
        existing.setReviewer(performance.getReviewer());
        existing.setReviewDate(performance.getReviewDate());

        Performance updated = repository.save(existing);

        NotificationRequest notification = new NotificationRequest();

        notification.setEmployeeId(updated.getEmployeeId());
        notification.setTitle("Performance Review Updated");
        notification.setMessage(
                "Your performance review has been updated by your manager."
        );
        notification.setType("INFO");

        notificationClient.send(notification);

        return updated;
    }

    // Delete Review
    public void delete(UUID id) {
        repository.deleteById(id);
    }
}