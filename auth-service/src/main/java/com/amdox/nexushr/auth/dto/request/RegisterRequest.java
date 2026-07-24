package com.amdox.nexushr.auth.dto.request;

import java.util.UUID;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RegisterRequest {

    @NotBlank(message = "Full name is required")
    @Size(max = 200)
    private String fullName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;

    // Changed to String for easier Swagger/Jackson handling
    private UUID tenantId;

    // Default role if none provided
    private String role = "EMPLOYEE";

    // ✅ No-arg constructor required by Jackson
    public RegisterRequest() {}

    // Optional all-args constructor
    public RegisterRequest(String fullName, String email, String password, UUID tenantId, String role) {
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.tenantId = tenantId;
        this.role = role;
    }

    // Getters and setters
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public UUID getTenantId() { return tenantId; }
    public void setTenantId(UUID tenantId) { this.tenantId = tenantId; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}
