package com.amdox.nexushr.employee.repository;

import com.amdox.nexushr.employee.entity.Employee;
import com.amdox.nexushr.employee.entity.EmployeeStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.Optional;
@Repository
public interface EmployeeRepository extends JpaRepository<Employee, UUID> {

	Optional<Employee> findTopByTenantIdOrderByEmployeeCodeDesc(UUID tenantId);
    // ONLY ACTIVE EMPLOYEES
    @Query("""
        SELECT e FROM Employee e
        WHERE e.tenantId = :tenantId
        AND e.active = true
        """)
    Page<Employee> findActiveEmployees(
            @Param("tenantId") UUID tenantId,
            Pageable pageable
    );


    Optional<Employee> findByIdAndTenantIdAndActiveTrue(
            UUID id,
            UUID tenantId
    );


    boolean existsByEmailAndTenantIdAndActiveTrue(
            String email,
            UUID tenantId
    );


    @Query("""
        SELECT e FROM Employee e
        WHERE e.tenantId = :tenantId
        AND e.active = true
        AND (
            LOWER(e.firstName) LIKE LOWER(CONCAT('%', :q, '%'))
            OR LOWER(e.lastName) LIKE LOWER(CONCAT('%', :q, '%'))
            OR LOWER(e.email) LIKE LOWER(CONCAT('%', :q, '%'))
        )
        """)
    Page<Employee> search(
            @Param("tenantId") UUID tenantId,
            @Param("q") String q,
            Pageable pageable
    );


    List<Employee> findByManager_IdAndActiveTrue(UUID managerId);


    @Query(value = """
        WITH RECURSIVE org_tree AS (

            SELECT
                id,
                first_name,
                last_name,
                designation,
                manager_id,
                department_id,
                status,
                0 AS depth

            FROM employees

            WHERE id = :rootId
            AND is_active = true


            UNION ALL


            SELECT
                e.id,
                e.first_name,
                e.last_name,
                e.designation,
                e.manager_id,
                e.department_id,
                e.status,
                ot.depth + 1

            FROM employees e

            INNER JOIN org_tree ot
            ON e.manager_id = ot.id

            WHERE e.is_active = true
            AND ot.depth < 10
        )

        SELECT *
        FROM org_tree
        ORDER BY depth,last_name

        """, nativeQuery = true)
    List<Object[]> getOrgChart(
            @Param("rootId") UUID rootId
    );


    long countByTenantIdAndActiveTrue(UUID tenantId);


    long countByTenantIdAndStatusAndActiveTrue(
            UUID tenantId,
            EmployeeStatus status
    );
}