package com.amdox.nexushr.payroll.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.UUID;

public class PayrollRequestDTO {

    @NotNull(message = "employeeId is required")
    private UUID employeeId;

    @NotBlank(message = "employeeName is required")
    private String employeeName;

    @NotBlank(message = "month is required")
    private String month;

    @NotNull(message = "year is required")
    private Integer year;

    @NotNull(message = "basicSalary is required")
    @Positive(message = "basicSalary must be positive")
    private BigDecimal basicSalary;

    // Optional overrides. If not supplied, service applies default percentages.
    private BigDecimal hra;
    private BigDecimal da;
    private BigDecimal specialAllowance;
    private BigDecimal otherDeductions;

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

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public BigDecimal getBasicSalary() {
        return basicSalary;
    }

    public void setBasicSalary(BigDecimal basicSalary) {
        this.basicSalary = basicSalary;
    }

    public BigDecimal getHra() {
        return hra;
    }

    public void setHra(BigDecimal hra) {
        this.hra = hra;
    }

    public BigDecimal getDa() {
        return da;
    }

    public void setDa(BigDecimal da) {
        this.da = da;
    }

    public BigDecimal getSpecialAllowance() {
        return specialAllowance;
    }

    public void setSpecialAllowance(BigDecimal specialAllowance) {
        this.specialAllowance = specialAllowance;
    }

    public BigDecimal getOtherDeductions() {
        return otherDeductions;
    }

    public void setOtherDeductions(BigDecimal otherDeductions) {
        this.otherDeductions = otherDeductions;
    }
}