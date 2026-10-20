package com.pcdoqtar.error;

public class ErrorResponse {

    private ErrorType type;
    private String message;

    public ErrorResponse(ErrorType type, String message) {
        this.type = type;
        this.message = message;
    }

    public ErrorType getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }
}