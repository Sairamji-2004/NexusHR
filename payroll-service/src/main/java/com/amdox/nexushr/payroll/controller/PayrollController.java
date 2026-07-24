package com.amdox.nexushr.payroll.controller;

import com.amdox.nexushr.common.response.ApiResponse;
import com.amdox.nexushr.payroll.dto.PayrollRequestDTO;
import com.amdox.nexushr.payroll.dto.PayrollResponseDTO;
import com.amdox.nexushr.payroll.security.AuthenticatedUser;
import com.amdox.nexushr.payroll.service.PayrollService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
@RestController
@RequestMapping("/api/payroll")
public class PayrollController {

    private final PayrollService payrollService;

    public PayrollController(PayrollService payrollService) {
        this.payrollService = payrollService;
    }

    /**
     * Extract tenantId from JWT Authentication
     */
    private UUID getTenantId(Authentication authentication) {

        if (authentication == null || authentication.getPrincipal() == null) {
            throw new RuntimeException("User is not authenticated");
        }

        Object principal = authentication.getPrincipal();

        if (!(principal instanceof AuthenticatedUser user)) {
            throw new RuntimeException(
                    "Invalid authentication principal: " + principal.getClass().getName()
            );
        }

        return user.getTenantId();
    }
   
    @PostMapping("/generate")
    public ResponseEntity<ApiResponse<PayrollResponseDTO>> generate(
            @Valid @RequestBody PayrollRequestDTO request,
            Authentication authentication) {

        System.out.println("========== PAYROLL CONTROLLER HIT ==========");
        System.out.println("Authentication: " + authentication);
        UUID tenantId = getTenantId(authentication);

        PayrollResponseDTO result =
                payrollService.generatePayroll(tenantId, request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(result, "Payroll generated successfully"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<PayrollResponseDTO>>> getAllPayrolls(
            Authentication authentication) {

        UUID tenantId = getTenantId(authentication);

        List<PayrollResponseDTO> payrolls =
                payrollService.getAllPayrolls(tenantId);

        if (payrolls == null) {
            payrolls = List.of();
        }

        return ResponseEntity.ok(
                ApiResponse.success(payrolls, "Payrolls fetched successfully")
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PayrollResponseDTO>> getPayrollById(
            @PathVariable Long id,
            Authentication authentication) {

        UUID tenantId = getTenantId(authentication);

        PayrollResponseDTO payroll =
                payrollService.getPayrollById(tenantId, id);

        return ResponseEntity.ok(
                ApiResponse.success(payroll, "Payroll fetched successfully")
        );
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<ApiResponse<List<PayrollResponseDTO>>> getPayrollByEmployee(
            @PathVariable UUID employeeId,
            Authentication authentication) {

        UUID tenantId = getTenantId(authentication);

        List<PayrollResponseDTO> payrolls =
                payrollService.getPayrollsByEmployee(tenantId, employeeId);

        return ResponseEntity.ok(
                ApiResponse.success(payrolls, "Employee payrolls fetched successfully")
        );
    }

    @GetMapping("/period")
    public ResponseEntity<ApiResponse<List<PayrollResponseDTO>>> getPayrollByPeriod(
            @RequestParam String month,
            @RequestParam Integer year,
            Authentication authentication) {

        UUID tenantId = getTenantId(authentication);

        List<PayrollResponseDTO> payrolls =
                payrollService.getPayrollsByMonthYear(tenantId, month, year);

        return ResponseEntity.ok(
                ApiResponse.success(payrolls, "Payrolls fetched successfully")
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> deletePayroll(
            @PathVariable Long id,
            Authentication authentication) {

        UUID tenantId = getTenantId(authentication);

        payrollService.deletePayroll(tenantId, id);

        return ResponseEntity.ok(
                ApiResponse.success(null, "Payroll deleted successfully")
        );
    }
    @GetMapping("/{id}/payslip")
    public ResponseEntity<byte[]> downloadPayslip(
            @PathVariable Long id,
            Authentication authentication) {

        UUID tenantId = getTenantId(authentication);

        byte[] pdf = payrollService.generatePayslip(tenantId, id);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=payslip-" + id + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> exportPayrollExcel(
            Authentication authentication) {

        UUID tenantId = getTenantId(authentication);

        byte[] excel = payrollService.exportPayrollExcel(tenantId);

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=Payroll.xlsx"
                )
                .contentType(
                        MediaType.parseMediaType(
                                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                        )
                )
                .body(excel);
    }
}