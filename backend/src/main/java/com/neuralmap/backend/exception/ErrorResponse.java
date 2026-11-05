package com.neuralmap.backend.exception;

/** The JSON the backend sends when something goes wrong. */
public record ErrorResponse(int status, String error, String message) {
}
