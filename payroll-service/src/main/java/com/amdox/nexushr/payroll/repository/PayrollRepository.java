package com.amdox.nexushr.payroll.repository;

import com.amdox.nexushr.payroll.entity.Payroll;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PayrollRepository extends JpaRepository<Payroll, Long> {

    List<Payroll> findByTenantIdAndEmployeeId(UUID tenantId, UUID employeeId);

    Optional<Payroll> findByTenantIdAndEmployeeIdAndMonthAndYear(
            UUID tenantId, UUID employeeId, String month, Integer year);

    List<Payroll> findByTenantIdAndMonthAndYear(UUID tenantId, String month, Integer year);

    List<Payroll> findByTenantId(UUID tenantId);

    Optional<Payroll> findByIdAndTenantId(Long id, UUID tenantId);
}