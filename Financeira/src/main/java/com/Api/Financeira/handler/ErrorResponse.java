package com.Api.Financeira.handler;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class ErrorResponse {

    private LocalDateTime timestamp;
    private String error;
    private String message;

    private int status;
    private String path;

    public ErrorResponse(String message, String error, String path, int status) {
        this.message = message;
        this.error = error;
        this.timestamp = LocalDateTime.now();
        this.path = path;
        this.status = status;
    }
}
