package com.amdox.nexushr.employee.repository;

import com.amdox.nexushr.employee.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, UUID> {
    List<Department> findByTenantIdAndActiveTrue(UUID tenantId);
    boolean existsByCodeAndTenantId(String code, UUID tenantId);
}
