package com.example.node.exceptions;

public class OptimisticLockingFailureException extends RuntimeException {
    public OptimisticLockingFailureException(String message) {
        super(message);
    }
}
