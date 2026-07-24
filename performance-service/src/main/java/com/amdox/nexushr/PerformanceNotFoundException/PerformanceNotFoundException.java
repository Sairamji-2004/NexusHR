package com.amdox.nexushr.PerformanceNotFoundException;

public class PerformanceNotFoundException extends RuntimeException {

    public PerformanceNotFoundException(String message) {
        super(message);
    }

    public PerformanceNotFoundException() {
        super("Performance review not found.");
    }
}