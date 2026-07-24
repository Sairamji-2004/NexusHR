package com.amdox.nexushr.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication(scanBasePackages = {
        "com.amdox.nexushr.auth",
        "com.amdox.nexushr.common"
})
@EnableJpaAuditing(auditorAwareRef = "springSecurityAuditorAware")
public class AuthServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(AuthServiceApplication.class, args);
    }
}
