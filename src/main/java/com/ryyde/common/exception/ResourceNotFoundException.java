package com.ryyde.common.exception;

/**
 * Thrown when a requested domain entity does not exist.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
