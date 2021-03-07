package com.btmatthews.testcontainers.mountebank;

public class MountebankContainerException extends RuntimeException {
    public MountebankContainerException(String message, Throwable cause) {
        super(message, cause);
    }
}
