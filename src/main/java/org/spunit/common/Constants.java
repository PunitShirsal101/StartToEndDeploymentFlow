package org.spunit.common;

import org.springframework.stereotype.Component;

@Component
public final class Constants {

    private Constants() {
        // utility class
    }

    // Error messages
    public static final String ERROR_CUSTOMER_NOT_FOUND = "Customer not found";

    // RFC7807
    public static final String RFC7807_VALIDATION_TYPE = "https://datatracker.ietf.org/doc/html/rfc7807#section-4.2";

    // Request tracing
    public static final String REQUEST_ID_HEADER = "X-Request-Id";
    public static final String REQUEST_ID_ATTRIBUTE = "requestId";
    public static final String MDC_REQUEST_ID_KEY = "requestId";

    // ProblemDetail / response properties
    public static final String PROP_TIMESTAMP = "timestamp";
    public static final String PROP_REQUEST_ID = "requestId";
}
