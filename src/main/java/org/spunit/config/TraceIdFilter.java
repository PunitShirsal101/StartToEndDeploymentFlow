package org.spunit.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.spunit.common.Constants;

import java.io.IOException;
import java.util.UUID;

@Component
public class TraceIdFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        String requestId = extractOrGenerateRequestId(request);
        request.setAttribute(Constants.REQUEST_ID_ATTRIBUTE, requestId);
        MDC.put(Constants.MDC_REQUEST_ID_KEY, requestId);
        response.setHeader(Constants.REQUEST_ID_HEADER, requestId);
        try {
            filterChain.doFilter(request, response);
        } finally {
            MDC.remove(Constants.MDC_REQUEST_ID_KEY);
        }
    }

    private String extractOrGenerateRequestId(HttpServletRequest request) {
        String incoming = request.getHeader(Constants.REQUEST_ID_HEADER);
        if (incoming != null && !incoming.isBlank()) {
            return incoming;
        }
        return UUID.randomUUID().toString();
    }
}