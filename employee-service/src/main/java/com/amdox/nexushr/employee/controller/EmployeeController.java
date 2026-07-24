package com.amdox.nexushr.employee.controller;

import com.amdox.nexushr.employee.dto.request.CreateEmployeeRequest;
import com.amdox.nexushr.employee.dto.response.EmployeeResponse;
import com.amdox.nexushr.employee.entity.EmployeeStatus;
import com.amdox.nexushr.employee.service.EmployeeService;
import com.amdox.nexushr.common.response.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.util.UUID;


@RestController
@RequestMapping("/api/v1/employees")
@Tag(name = "Employees", description = "Employee lifecycle management")
@SecurityRequirement(name = "bearerAuth")
public class EmployeeController {


    private static final UUID DEFAULT_TENANT =
            UUID.fromString("00000000-0000-0000-0000-000000000001");


    private final EmployeeService employeeService;


    public EmployeeController(EmployeeService employeeService) {

        this.employeeService = employeeService;

    }



    @PostMapping
    @Operation(summary = "Create employee")
    public ResponseEntity<ApiResponse<EmployeeResponse>> create(
            @Valid @RequestBody CreateEmployeeRequest request,
            HttpServletRequest req) {


        UUID tenantId = getTenantId(req);


        EmployeeResponse response =
                employeeService.create(request, tenantId);


        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        ApiResponse.success(
                                response,
                                "Employee created successfully"
                        )
                );
    }




    @GetMapping("/export")
    @Operation(summary = "Export employees to Excel")
    public ResponseEntity<byte[]> exportEmployees(
            HttpServletRequest req) {


        byte[] excel =
                employeeService.exportEmployees(
                        getTenantId(req)
                );


        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=Employees.xlsx"
                )
                .contentType(
                        MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                        )
                )
                .body(excel);

    }




    @GetMapping("/{id}")
    @Operation(summary = "Get employee by ID")
    public ResponseEntity<ApiResponse<EmployeeResponse>> getById(
            @PathVariable UUID id,
            HttpServletRequest req) {


        EmployeeResponse response =
                employeeService.findById(
                        id,
                        getTenantId(req)
                );


        return ResponseEntity.ok(
                ApiResponse.success(response)
        );

    }





    @GetMapping
    @Operation(summary = "List all employees with search and pagination")
    public ResponseEntity<ApiResponse<Page<EmployeeResponse>>> getAll(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            HttpServletRequest req) {


        PageRequest pageable =
                PageRequest.of(
                        page,
                        size,
                        Sort.by("lastName").ascending()
                );


        Page<EmployeeResponse> result =
                employeeService.findAll(
                        getTenantId(req),
                        search,
                        pageable
                );


        return ResponseEntity.ok(
                ApiResponse.success(result)
        );

    }





    @PatchMapping("/{id}/status")
    @Operation(summary = "Update employee status")
    public ResponseEntity<ApiResponse<EmployeeResponse>> updateStatus(
            @PathVariable UUID id,
            @RequestParam EmployeeStatus status,
            HttpServletRequest req) {


        EmployeeResponse response =
                employeeService.updateStatus(
                        id,
                        getTenantId(req),
                        status
                );


        return ResponseEntity.ok(
                ApiResponse.success(
                        response,
                        "Status updated"
                )
        );

    }





    @DeleteMapping("/{id}")
    @Operation(summary = "Soft delete employee")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable UUID id,
            HttpServletRequest req) {


        employeeService.softDelete(
                id,
                getTenantId(req)
        );


        return ResponseEntity.ok(
                ApiResponse.success(
                        null,
                        "Employee offboarded successfully"
                )
        );

    }





    private UUID getTenantId(HttpServletRequest req) {


        String tenantId =
                req.getHeader("X-Tenant-Id");


        return tenantId != null
                ? UUID.fromString(tenantId)
                : DEFAULT_TENANT;

    }

}