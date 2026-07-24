package com.amdox.nexushr.PerformanceRepository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.amdox.nexushr.entity.Performance;

@Repository
public interface PerformanceRepository extends JpaRepository<Performance, UUID> {

    List<Performance> findByEmployeeId(UUID employeeId);

}