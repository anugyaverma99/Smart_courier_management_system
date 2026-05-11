package com.CourierManagement.AdminService.Exception;

import lombok.Getter;

@Getter
public class AdminServiceException extends RuntimeException {
    private final int statusCode;

    public AdminServiceException(String message) {
        super(message);
        this.statusCode = 404; // default
    }

    public AdminServiceException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }
}
