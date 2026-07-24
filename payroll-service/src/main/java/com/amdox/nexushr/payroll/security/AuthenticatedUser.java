package com.amdox.nexushr.payroll.security;

import java.util.UUID;

/**
 * Set as the Authentication principal by JwtAuthFilter so controllers/services
 * can pull the current user's id and tenant without re-parsing the token.
 */
public class AuthenticatedUser {

    private final UUID userId;
    private final UUID tenantId;
    private final String role;

    public AuthenticatedUser(UUID userId, UUID tenantId, String role) {
        this.userId = userId;
        this.tenantId = tenantId;
        this.role = role;
    }

    public UUID getUserId() {
        return userId;
    }

    public UUID getTenantId() {
        return tenantId;
    }

    public String getRole() {
        return role;
    }
}
