package com.example.connector.exception;

public class NoSQLConnectionException extends RuntimeException {
    public NoSQLConnectionException(String message) {
        super(message);
    }
}
