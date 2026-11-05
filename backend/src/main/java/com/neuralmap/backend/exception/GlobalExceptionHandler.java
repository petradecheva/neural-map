package com.neuralmap.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;


@RestControllerAdvice
public class GlobalExceptionHandler {

    // Our own validation errors (empty file, not a PDF, ...) -> 400 Bad Request
    @ExceptionHandler(InvalidDocumentException.class)
    public ResponseEntity<ErrorResponse> handleInvalidDocument(InvalidDocumentException ex) {
        return build(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    // File bigger than the limit in application.properties -> 413 Payload Too Large
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ErrorResponse> handleTooLarge(MaxUploadSizeExceededException ex) {
        return build(HttpStatus.PAYLOAD_TOO_LARGE, "The file is too large. The maximum size is 20 MB.");
    }

    // Request was not a file upload, or the "file" field is missing -> 400 Bad Request
    @ExceptionHandler(MultipartException.class)
    public ResponseEntity<ErrorResponse> handleBadUpload(MultipartException ex) {
        return build(HttpStatus.BAD_REQUEST, "Please upload a file in the form field named 'file'.");
    }

    // One place that builds every error answer, so they all have the same shape.
    private ResponseEntity<ErrorResponse> build(HttpStatus status, String message) {
        ErrorResponse body = new ErrorResponse(status.value(), status.getReasonPhrase(), message);
        return ResponseEntity.status(status).body(body);
    }
}
