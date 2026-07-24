package com.amdox.nexushr.employee.dto.request;

import com.amdox.nexushr.employee.entity.EmploymentType;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public class CreateEmployeeRequest {
    @NotBlank(message = "First name required")
    private String firstName;
    @NotBlank(message = "Last name required")
    private String lastName;
    @NotBlank @Email
    private String email;
    private String phone;
    @NotNull(message = "Hire date required")
    private LocalDate hireDate;
    private LocalDate dateOfBirth;
    private String jobTitle;
    private BigDecimal currentSalary;
    private UUID departmentId;
    private UUID managerId;
    private EmploymentType employmentType = EmploymentType.FULL_TIME;

    public String getFirstName() { return firstName; }
    public void setFirstName(String v) { this.firstName = v; }
    public String getLastName() { return lastName; }
    public void setLastName(String v) { this.lastName = v; }
    public String getEmail() { return email; }
    public void setEmail(String v) { this.email = v; }
    public String getPhone() { return phone; }
    public void setPhone(String v) { this.phone = v; }
    public LocalDate getHireDate() { return hireDate; }
    public void setHireDate(LocalDate v) { this.hireDate = v; }
    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate v) { this.dateOfBirth = v; }
    public String getJobTitle() { return jobTitle; }
    public void setJobTitle(String v) { this.jobTitle = v; }
    public BigDecimal getCurrentSalary() { return currentSalary; }
    public void setCurrentSalary(BigDecimal v) { this.currentSalary = v; }
    public UUID getDepartmentId() { return departmentId; }
    public void setDepartmentId(UUID v) { this.departmentId = v; }
    public UUID getManagerId() { return managerId; }
    public void setManagerId(UUID v) { this.managerId = v; }
    public EmploymentType getEmploymentType() { return employmentType; }
    public void setEmploymentType(EmploymentType v) { this.employmentType = v; }
}
