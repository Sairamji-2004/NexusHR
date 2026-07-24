package com.amdox.nexushr.employee.dto.response;

import com.amdox.nexushr.employee.entity.Employee;
import com.amdox.nexushr.employee.entity.EmployeeStatus;
import com.amdox.nexushr.employee.entity.EmploymentType;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public class EmployeeResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private UUID id;

    private String employeeCode;

    private String firstName;

    private String lastName;

    private String fullName;

    private String email;

    private String phone;

    private String designation;

    private EmployeeStatus status;

    private EmploymentType employmentType;

    private LocalDate hireDate;

    private BigDecimal currentCtc;

    private String profilePhotoUrl;

    private String departmentName;

    private UUID departmentId;

    private UUID managerId;

    private String managerName;


    public static EmployeeResponse from(Employee e) {

        EmployeeResponse r = new EmployeeResponse();

        r.id = e.getId();
        r.employeeCode = e.getEmployeeCode();

        r.firstName = e.getFirstName();
        r.lastName = e.getLastName();

        // Important for Leave Service
        r.fullName = e.getFullName();

        r.email = e.getEmail();
        r.phone = e.getPhone();
        r.designation = e.getDesignation();

        r.status = e.getStatus();

        r.employmentType = e.getEmploymentType();

        r.hireDate = e.getHireDate();

        r.currentCtc = e.getCurrentCtc();

        r.profilePhotoUrl = e.getProfilePhotoUrl();


        if (e.getDepartment() != null) {

            r.departmentId = e.getDepartment().getId();

            r.departmentName = e.getDepartment().getName();
        }


        if (e.getManager() != null) {

            r.managerId = e.getManager().getId();

            r.managerName = e.getManager().getFullName();
        }


        return r;
    }



    public UUID getId() {
        return id;
    }


    public String getEmployeeCode() {
        return employeeCode;
    }


    public String getFirstName() {
        return firstName;
    }


    public String getLastName() {
        return lastName;
    }


    public String getFullName() {
        return fullName;
    }


    public String getEmail() {
        return email;
    }


    public String getPhone() {
        return phone;
    }


    public String getDesignation() {
        return designation;
    }


    public EmployeeStatus getStatus() {
        return status;
    }


    public EmploymentType getEmploymentType() {
        return employmentType;
    }


    public LocalDate getHireDate() {
        return hireDate;
    }


    public BigDecimal getCurrentCtc() {
        return currentCtc;
    }


    public String getProfilePhotoUrl() {
        return profilePhotoUrl;
    }


    public String getDepartmentName() {
        return departmentName;
    }


    public UUID getDepartmentId() {
        return departmentId;
    }


    public UUID getManagerId() {
        return managerId;
    }


    public String getManagerName() {
        return managerName;
    }
}