package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
public class ErrorResponse {
    private String message;
    private int statusCode;
    private long timestamp;

    private Map<String, String> validationErrors;

    public ErrorResponse(String message, int statusCode, long timestamp) {
        this.message = message;
        this.statusCode = statusCode;
        this.timestamp = timestamp;
    }
}