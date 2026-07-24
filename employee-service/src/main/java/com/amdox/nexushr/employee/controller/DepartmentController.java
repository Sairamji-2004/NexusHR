package com.amdox.nexushr.employee.controller;

import com.amdox.nexushr.employee.entity.Department;
import com.amdox.nexushr.employee.repository.DepartmentRepository;
import com.amdox.nexushr.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/departments")
@Tag(name = "Departments", description = "Department management")
public class DepartmentController {

    private static final UUID DEFAULT_TENANT = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private final DepartmentRepository departmentRepository;

    public DepartmentController(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Department>>> getAll() {
        List<Department> departments = departmentRepository.findByTenantIdAndActiveTrue(DEFAULT_TENANT);
        return ResponseEntity.ok(ApiResponse.success(departments));
    }
}
