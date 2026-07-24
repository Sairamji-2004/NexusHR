package com.amdox.nexushr.dto;

import java.time.LocalDate;
import java.util.UUID;

import com.amdox.nexushr.enums.PerformanceRating;



public class PerformanceRequest {

    private UUID employeeId;
    private String employeeName;
    private String department;
    private String reviewer;
    private LocalDate reviewDate;
    private PerformanceRating rating;
    private String comments;

    public PerformanceRequest() {
    }

    public UUID getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(UUID employeeId) {
        this.employeeId = employeeId;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getReviewer() {
        return reviewer;
    }

    public void setReviewer(String reviewer) {
        this.reviewer = reviewer;
    }

    public LocalDate getReviewDate() {
        return reviewDate;
    }

    public void setReviewDate(LocalDate reviewDate) {
        this.reviewDate = reviewDate;
    }

    public PerformanceRating getRating() {
        return rating;
    }

    public void setRating(PerformanceRating rating) {
        this.rating = rating;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }
}