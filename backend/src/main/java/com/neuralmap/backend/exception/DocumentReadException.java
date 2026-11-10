package com.neuralmap.backend.exception;

public class DocumentReadException extends RuntimeException {
    public DocumentReadException(String message, Throwable cause) {
        super(message, cause);
    }
}