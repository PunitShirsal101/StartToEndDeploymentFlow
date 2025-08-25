package org.spunit.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;

@JsonInclude(JsonInclude.Include.NON_NULL)
@SuppressWarnings("unused")
public class ApiResponse {
    private boolean success;
    private Object data;
    private String message;
    private Instant timestamp;
    private String requestId;

    public ApiResponse(boolean success, Object data, String message, String requestId) {
        this.success = success;
        this.data = data;
        this.message = message;
        this.requestId = requestId;
        this.timestamp = Instant.now();
    }

    public static ApiResponse ok(Object data, String requestId) {
        return new ApiResponse(true, data, null, requestId);
    }

    public static ApiResponse created(Object data, String requestId) {
        return new ApiResponse(true, data, null, requestId);
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }
}