package com.wellnr.ddd;

public class DomainException extends RuntimeException {

    private final int status;

    private final String summary;

    public DomainException(String summary, String message, Throwable cause, int status) {
        super(message, cause);
        this.summary = summary;
        this.status = status;
    }

    public DomainException(String summary, String message, int status) {
        this(summary, message, null, status);
    }

    public DomainException(String summary, String message, Throwable cause) {
        this(summary, message, cause, 406);
    }

    public DomainException(String summary, String message) {
        this(summary, message, null, 406);
    }

    public DomainException(String message, int status) {
        this(message, message, null, status);
    }

    public DomainException(String message, Throwable cause) {
        this(message, message, cause, 406);
    }

    public DomainException(String message) {
        this(message, message, null, 406);
    }

    public int getStatus() {
        return status;
    }

    public String getSummary() {
        return summary;
    }
}
