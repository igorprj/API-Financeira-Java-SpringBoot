package com.Api.Financeira.exceptions;

public class AccessDeniedTransactionException extends RuntimeException {
    public AccessDeniedTransactionException(String message) {
        super(message);
    }
}
