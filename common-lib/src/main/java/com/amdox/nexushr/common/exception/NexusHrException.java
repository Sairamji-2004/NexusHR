package com.amdox.nexushr.common.exception;

public class NexusHrException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public NexusHrException(String message) {
        super(message);
    }

    public NexusHrException(String message, Throwable cause) {
        super(message, cause);
    }
}
