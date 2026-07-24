package com.amdox.nexushr.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class NexusAccessDeniedException extends NexusHrException {
    private static final long serialVersionUID = 1L;

    public NexusAccessDeniedException(String message) {
        super(message);
    }
}
