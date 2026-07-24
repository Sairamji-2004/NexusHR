package com.amdox.nexushr.auth.service;

import com.amdox.nexushr.auth.dto.request.LoginRequest;
import com.amdox.nexushr.auth.dto.request.RefreshRequest;
import com.amdox.nexushr.auth.dto.request.RegisterRequest;
import com.amdox.nexushr.auth.dto.response.AuthResponse;
import com.amdox.nexushr.auth.entity.RefreshToken;
import com.amdox.nexushr.auth.entity.Role;
import com.amdox.nexushr.auth.entity.User;
import com.amdox.nexushr.auth.repository.RefreshTokenRepository;
import com.amdox.nexushr.auth.repository.RoleRepository;
import com.amdox.nexushr.auth.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Base64;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Transactional
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);
    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final long LOCKOUT_MINUTES = 30;

    // Default tenant ID for demo (matches our Flyway seed)
    private static final UUID DEFAULT_TENANT_ID =
            UUID.fromString("00000000-0000-0000-0000-000000000001");

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final StringRedisTemplate redisTemplate;

    public AuthService(UserRepository userRepository,
                       RoleRepository roleRepository,
                       RefreshTokenRepository refreshTokenRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService,
                       StringRedisTemplate redisTemplate) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.redisTemplate = redisTemplate;
    }

    // ── REGISTER ─────────────────────────────────────────────────────
    public AuthResponse register(RegisterRequest request) {
        UUID tenantId = request.getTenantId() != null
                ? request.getTenantId() : DEFAULT_TENANT_ID;

        // Check duplicate email within tenant
        if (userRepository.existsByEmailAndTenantId(request.getEmail(), tenantId)) {
            throw new RuntimeException("Email already registered in this organisation");
        }

        // Find role (default to ROLE_EMPLOYEE)
        String roleName = request.getRole() != null
                ? request.getRole() : "ROLE_EMPLOYEE";
        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new RuntimeException("Role not found: " + roleName));

        // Create user
        User user = new User();
        user.setTenantId(tenantId);
        user.setEmail(request.getEmail().toLowerCase().trim());
        user.setFullName(request.getFullName());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRoles(Set.of(role));
        user = userRepository.save(user);

        log.info("User registered: {} in tenant: {}", user.getEmail(), tenantId);

        return buildAuthResponse(user, roleName);
    }

    // ── LOGIN ─────────────────────────────────────────────────────────
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail().toLowerCase().trim())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        // Check account locked
        if (user.isAccountLocked()) {
            throw new RuntimeException("Account locked. Try again after " + user.getLockedUntil());
        }

        // Check account active
        if (!user.isActive()) {
            throw new RuntimeException("Account is disabled. Contact HR administrator.");
        }

        // Verify password
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            handleFailedLogin(user);
            throw new RuntimeException("Invalid email or password");
        }

        // Reset failed attempts on success
        user.setFailedLoginCount(0);
        user.setLockedUntil(null);
        user.setLastLoginAt(Instant.now());
        userRepository.save(user);

        String primaryRole = user.getRoles().stream()
                .map(Role::getName)
                .findFirst()
                .orElse("ROLE_EMPLOYEE");

        log.info("User logged in: {}", user.getEmail());
        return buildAuthResponse(user, primaryRole);
    }

    // ── REFRESH TOKEN ─────────────────────────────────────────────────
    public AuthResponse refresh(RefreshRequest request) {
        String tokenHash = hashToken(request.getRefreshToken());

        RefreshToken refreshToken = refreshTokenRepository.findByTokenHash(tokenHash)
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

        if (refreshToken.isRevoked()) {
            throw new RuntimeException("Refresh token has been revoked");
        }

        if (refreshToken.isExpired()) {
            throw new RuntimeException("Refresh token has expired. Please login again.");
        }

        User user = refreshToken.getUser();
        String primaryRole = user.getRoles().stream()
                .map(Role::getName)
                .findFirst()
                .orElse("ROLE_EMPLOYEE");

        // Revoke old refresh token and issue new one
        refreshToken.setRevoked(true);
        refreshTokenRepository.save(refreshToken);

        return buildAuthResponse(user, primaryRole);
    }

    // ── LOGOUT ───────────────────────────────────────────────────────
    public void logout(String accessToken, UUID userId) {
        // Blacklist access token in Redis until it expires
        if (accessToken != null && accessToken.startsWith("Bearer ")) {
            String token = accessToken.substring(7);
            try {
                long expirySeconds = jwtService.getAccessTokenExpirySeconds();
                redisTemplate.opsForValue()
                        .set("blacklist:" + token, "1", expirySeconds, TimeUnit.SECONDS);
            } catch (Exception e) {
                log.warn("Could not blacklist token in Redis: {}", e.getMessage());
            }
        }

        // Revoke all refresh tokens for this user
        refreshTokenRepository.revokeAllByUserId(userId);
        log.info("User logged out: {}", userId);
    }

    // ── Private helpers ───────────────────────────────────────────────

    private AuthResponse buildAuthResponse(User user, String primaryRole) {
        String accessToken = jwtService.generateAccessToken(
                user.getId(), user.getEmail(), primaryRole, user.getTenantId());
        String rawRefreshToken = jwtService.generateRefreshToken(user.getId());

        // Store refresh token hash in DB
        RefreshToken rt = new RefreshToken();
        rt.setUser(user);
        rt.setTokenHash(hashToken(rawRefreshToken));
        rt.setExpiresAt(Instant.now().plusMillis(jwtService.getRefreshTokenExpiryMs()));
        refreshTokenRepository.save(rt);

        Set<String> roleNames = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toSet());

        return AuthResponse.of(
                accessToken,
                rawRefreshToken,
                jwtService.getAccessTokenExpirySeconds(),
                user.getId(),
                user.getEmployeeId(),   // NEW
                user.getEmail(),
                user.getFullName(),
                roleNames,
                user.getTenantId()
        );
    }

    private void handleFailedLogin(User user) {
        int attempts = user.getFailedLoginCount() + 1;
        user.setFailedLoginCount(attempts);
        if (attempts >= MAX_FAILED_ATTEMPTS) {
            user.setLockedUntil(Instant.now().plusSeconds(LOCKOUT_MINUTES * 60));
            log.warn("Account locked after {} failed attempts: {}", attempts, user.getEmail());
        }
        userRepository.save(user);
    }

    private String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException("Failed to hash token", e);
        }
    }
}
