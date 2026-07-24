package com.amdox.nexushr.employee.service;

import com.amdox.nexushr.employee.dto.request.CreateEmployeeRequest;
import com.amdox.nexushr.employee.dto.request.NotificationRequest;
import com.amdox.nexushr.employee.client.NotificationClient;
import com.amdox.nexushr.employee.dto.response.EmployeeResponse;
import com.amdox.nexushr.employee.entity.Employee;
import com.amdox.nexushr.employee.entity.EmployeeStatus;
import com.amdox.nexushr.employee.repository.DepartmentRepository;
import com.amdox.nexushr.employee.repository.EmployeeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.UUID;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
@Service
@Transactional
public class EmployeeService implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger log = LoggerFactory.getLogger(EmployeeService.class);

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final NotificationClient notificationClient;
    public EmployeeService(
            EmployeeRepository employeeRepository,
            DepartmentRepository departmentRepository,
            NotificationClient notificationClient) {

        this.employeeRepository = employeeRepository;
        this.departmentRepository = departmentRepository;
        this.notificationClient = notificationClient;
    }
    public EmployeeResponse create(CreateEmployeeRequest req, UUID tenantId) {

        try {

            if (employeeRepository.existsByEmailAndTenantIdAndActiveTrue(req.getEmail(), tenantId)) {
                throw new IllegalStateException("Employee with email already exists.");
            }

            Employee e = new Employee();

            e.setTenantId(tenantId);
            e.setEmployeeCode(generateCode(tenantId));
            e.setFirstName(req.getFirstName());
            e.setLastName(req.getLastName());
            e.setEmail(req.getEmail().trim().toLowerCase());
            e.setPhone(req.getPhone());
            e.setHireDate(req.getHireDate());
            e.setDateOfBirth(req.getDateOfBirth());
            e.setDesignation(req.getJobTitle());
            e.setCurrentCtc(req.getCurrentSalary());
            e.setEmploymentType(req.getEmploymentType());
            e.setStatus(EmployeeStatus.ACTIVE);
            e.setActive(true);

            if (req.getDepartmentId() != null) {
                departmentRepository.findById(req.getDepartmentId())
                        .ifPresent(e::setDepartment);
            }

            if (req.getManagerId() != null) {
                employeeRepository.findById(req.getManagerId())
                        .ifPresent(e::setManager);
            }

            log.info("========== BEFORE SAVE ==========");
            log.info("Employee Code : {}", e.getEmployeeCode());
            log.info("Email         : {}", e.getEmail());
            log.info("Tenant ID     : {}", e.getTenantId());

            Employee saved = employeeRepository.save(e);

            log.info("========== AFTER SAVE ==========");
            log.info("Saved Employee ID : {}", saved.getId());

            NotificationRequest notification = new NotificationRequest(
                    saved.getId(),
                    "Welcome to NexusHR",
                    "Welcome " + saved.getFirstName()
                            + "! Your employee account has been created successfully.",
                    "SUCCESS"
            );
            System.out.println("BEFORE notificationClient.send()");

            notificationClient.send(notification);
            System.out.println("AFTER notificationClient.send()");
            log.info("========== EMPLOYEE NOTIFICATION ==========");
            log.info("Employee ID : {}", saved.getId());
            log.info("Welcome notification sent.");

            return EmployeeResponse.from(saved);

        } catch (Exception ex) {

            log.error("Employee creation failed!", ex);
            throw ex;

        }
    }
    @Cacheable(value = "employees", key = "#id + ':' + #tenantId")
    public EmployeeResponse findById(UUID id, UUID tenantId) {
        Employee employee = employeeRepository
                .findByIdAndTenantIdAndActiveTrue(id, tenantId)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found for ID: " + id));

        return EmployeeResponse.from(employee);
    }

    public Page<EmployeeResponse> findAll(
            UUID tenantId,
            String search,
            Pageable pageable
    ) {

        if(search != null && !search.isBlank()) {

            return employeeRepository.search(
                    tenantId,
                    search,
                    pageable
            ).map(EmployeeResponse::from);

        }


        return employeeRepository.findActiveEmployees(
                tenantId,
                pageable
        ).map(EmployeeResponse::from);
    }

    @CacheEvict(value = "employees", key = "#id + ':' + #tenantId")
    public EmployeeResponse updateStatus(UUID id, UUID tenantId, EmployeeStatus status) {
        Employee employee = employeeRepository
                .findByIdAndTenantIdAndActiveTrue(id, tenantId)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found for ID: " + id));

        employee.setStatus(status);

        Employee updated = employeeRepository.save(employee);

        log.info("Employee status updated: {} -> {}", id, status);

        return EmployeeResponse.from(updated);
    }

    @CacheEvict(value = "employees", key = "#id + ':' + #tenantId")
    public void softDelete(UUID id, UUID tenantId) {
        Employee employee = employeeRepository
                .findByIdAndTenantIdAndActiveTrue(id, tenantId)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found for ID: " + id));

        employee.setActive(false);
        employee.setStatus(EmployeeStatus.TERMINATED);

        employeeRepository.save(employee);

        log.info("Employee deactivated: {}", id);
    }
 
    public byte[] exportEmployees(UUID tenantId) {

        List<Employee> employees = employeeRepository
                .findActiveEmployees(
                        tenantId,
                        org.springframework.data.domain.Pageable.unpaged()
                )
                .getContent();

        try (Workbook workbook = new XSSFWorkbook()) {

            Sheet sheet = workbook.createSheet("Employees");

            Row header = sheet.createRow(0);

            header.createCell(0).setCellValue("Employee Code");
            header.createCell(1).setCellValue("Name");
            header.createCell(2).setCellValue("Email");
            header.createCell(3).setCellValue("Phone");
            header.createCell(4).setCellValue("Designation");
            header.createCell(5).setCellValue("Department");
            header.createCell(6).setCellValue("Status");
            header.createCell(7).setCellValue("Employment Type");
            header.createCell(8).setCellValue("Salary");

            int rowNum = 1;

            for (Employee e : employees) {

                Row row = sheet.createRow(rowNum++);

                row.createCell(0).setCellValue(e.getEmployeeCode());

                row.createCell(1).setCellValue(
                        e.getFirstName() + " " + e.getLastName()
                );

                row.createCell(2).setCellValue(e.getEmail());

                row.createCell(3).setCellValue(e.getPhone());

                row.createCell(4).setCellValue(e.getDesignation());

                row.createCell(5).setCellValue(
                        e.getDepartment() != null
                                ? e.getDepartment().getName()
                                : ""
                );

                row.createCell(6).setCellValue(
                        e.getStatus().name()
                );

                row.createCell(7).setCellValue(
                        e.getEmploymentType().name()
                );

                row.createCell(8).setCellValue(
                        e.getCurrentCtc().doubleValue()
                );
            }

            for (int i = 0; i < 9; i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();

            workbook.write(out);

            return out.toByteArray();

        } catch (IOException ex) {

            throw new RuntimeException("Unable to export employees", ex);

        }
    }
    
    private String generateCode(UUID tenantId) {

        return employeeRepository
                .findTopByTenantIdOrderByEmployeeCodeDesc(tenantId)
                .map(employee -> {
                    String lastCode = employee.getEmployeeCode();

                    if (lastCode == null || lastCode.isBlank()) {
                        return "EMP00001";
                    }

                    try {
                        int number = Integer.parseInt(lastCode.substring(3));
                        return String.format("EMP%05d", number + 1);
                    } catch (Exception ex) {
                        log.warn("Invalid employee code found: {}. Resetting to EMP00001", lastCode);
                        return "EMP00001";
                    }
                })
                .orElse("EMP00001");
    }
}