package com.amdox.nexushr.auth.dto.response;

import java.util.Set;
import java.util.UUID;

public class AuthResponse {

    private String accessToken;
    private String refreshToken;
    private String tokenType = "Bearer";
    private long expiresIn;

    private UUID userId;

    // NEW
    private UUID employeeId;

    private String email;
    private String fullName;
    private Set<String> roles;
    private UUID tenantId;

    public static AuthResponse of(
            String accessToken,
            String refreshToken,
            long expiresIn,
            UUID userId,
            UUID employeeId,
            String email,
            String fullName,
            Set<String> roles,
            UUID tenantId) {

        AuthResponse r = new AuthResponse();

        r.accessToken = accessToken;
        r.refreshToken = refreshToken;
        r.expiresIn = expiresIn;

        r.userId = userId;
        r.employeeId = employeeId;

        r.email = email;
        r.fullName = fullName;
        r.roles = roles;
        r.tenantId = tenantId;

        return r;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public long getExpiresIn() {
        return expiresIn;
    }

    public UUID getUserId() {
        return userId;
    }

    public UUID getEmployeeId() {
        return employeeId;
    }

    public String getEmail() {
        return email;
    }

    public String getFullName() {
        return fullName;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public UUID getTenantId() {
        return tenantId;
    }
}