package com.amdox.nexushr.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import com.amdox.nexushr.enums.PerformanceRating;
import com.amdox.nexushr.enums.ReviewStatus;

import jakarta.persistence.*;

@Entity
@Table(name = "performance")
public class Performance {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private UUID employeeId;

    @Column(nullable = false)
    private String employeeName;

    @Column(nullable = false)
    private String department;

    @Column(nullable = false)
    private String reviewer;

    @Column(nullable = false)
    private LocalDate reviewDate;

    @Column
    private String feedback;
    
    @Enumerated(EnumType.STRING)
    private PerformanceRating rating;

    @Column(length = 2000)
    private String comments;

    @Enumerated(EnumType.STRING)
    private ReviewStatus status = ReviewStatus.PENDING;

    private LocalDateTime createdAt = LocalDateTime.now();

    public Performance() {
    }

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
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

	public ReviewStatus getStatus() {
		return status;
	}

	public void setStatus(ReviewStatus status) {
		this.status = status;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
	public String getFeedback() {
	    return feedback;
	}

	public void setFeedback(String feedback) {
	    this.feedback = feedback;
	}
    // Generate Getters and Setters
}