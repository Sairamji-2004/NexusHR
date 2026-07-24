package com.amdox.nexushr.auth.controller;

import com.amdox.nexushr.auth.dto.request.LoginRequest;
import com.amdox.nexushr.auth.dto.request.RefreshRequest;
import com.amdox.nexushr.auth.dto.request.RegisterRequest;
import com.amdox.nexushr.auth.dto.response.AuthResponse;
import com.amdox.nexushr.auth.service.AuthService;
import com.amdox.nexushr.auth.service.JwtService;
import com.amdox.nexushr.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * AuthController — all authentication endpoints.
 *
 * POST /api/v1/auth/register  — create account
 * POST /api/v1/auth/login     — login, get JWT
 * POST /api/v1/auth/refresh   — get new access token
 * POST /api/v1/auth/logout    — invalidate tokens
 */
@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication", description = "JWT-based authentication endpoints")
public class AuthController {

    private final AuthService authService;
    private final JwtService jwtService;

    public AuthController(AuthService authService, JwtService jwtService) {
        this.authService = authService;
        this.jwtService = jwtService;
    }

    // ── POST /api/v1/auth/register ────────────────────────────────────
    @PostMapping("/register")
    @Operation(summary = "Register a new user",
               description = "Creates a new user account and returns JWT tokens")
    public ResponseEntity<ApiResponse<AuthResponse>> register(
            @Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Registration successful"));
    }

    // ── POST /api/v1/auth/login ───────────────────────────────────────
    @PostMapping("/login")
    @Operation(summary = "Login",
               description = "Authenticate with email and password, returns JWT tokens")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success(response, "Login successful"));
    }

    // ── POST /api/v1/auth/refresh ─────────────────────────────────────
    @PostMapping("/refresh")
    @Operation(summary = "Refresh access token",
               description = "Exchange a valid refresh token for a new access token")
    public ResponseEntity<ApiResponse<AuthResponse>> refresh(
            @Valid @RequestBody RefreshRequest request) {
        AuthResponse response = authService.refresh(request);
        return ResponseEntity.ok(ApiResponse.success(response, "Token refreshed"));
    }

    // ── POST /api/v1/auth/logout ──────────────────────────────────────
    @PostMapping("/logout")
    @Operation(summary = "Logout",
               description = "Invalidate access token and all refresh tokens")
    public ResponseEntity<ApiResponse<Void>> logout(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        // Extract userId from token
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                UUID userId = jwtService.extractUserId(token);
                authService.logout(authHeader, userId);
            } catch (Exception e) {
                // Token already invalid — still return success
            }
        }
        return ResponseEntity.ok(ApiResponse.success(null, "Logged out successfully"));
    }

    // ── GET /api/v1/auth/health ───────────────────────────────────────
    @GetMapping("/health")
    @Operation(summary = "Health check")
    public ResponseEntity<ApiResponse<String>> health() {
        return ResponseEntity.ok(ApiResponse.success("Auth service is running", "OK"));
    }
}
