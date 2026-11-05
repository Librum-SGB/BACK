package com.sgb.mylibrum.config;

import java.io.IOException;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Registra uma linha por requisição HTTP (método, caminho, status e duração) e publica um ID de
 * correlação no MDC. Não registra query string, headers nem corpo, para não vazar tokens ou dados pessoais.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RequestLoggingFilter extends OncePerRequestFilter {

    public static final String HEADER_REQUEST_ID = "X-Request-Id";
    public static final String MDC_REQUEST_ID = "requestId";

    private static final Logger log = LoggerFactory.getLogger("http.requests");

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String uri = request.getRequestURI();
        return uri.startsWith("/actuator/health");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        String requestId = sanitizar(request.getHeader(HEADER_REQUEST_ID));
        if (requestId == null) {
            requestId = UUID.randomUUID().toString().substring(0, 8);
        }
        MDC.put(MDC_REQUEST_ID, requestId);
        response.setHeader(HEADER_REQUEST_ID, requestId);

        long inicio = System.nanoTime();
        try {
            filterChain.doFilter(request, response);
        } finally {
            long ms = (System.nanoTime() - inicio) / 1_000_000;
            int status = response.getStatus();
            String msg = "{} {} -> {} ({} ms)";
            if (status >= 500) {
                log.error(msg, request.getMethod(), request.getRequestURI(), status, ms);
            } else if (status >= 400) {
                log.warn(msg, request.getMethod(), request.getRequestURI(), status, ms);
            } else {
                log.info(msg, request.getMethod(), request.getRequestURI(), status, ms);
            }
            MDC.clear();
        }
    }

    // Aceita apenas IDs curtos e seguros vindos do cliente (evita injeção de quebras de linha nos logs).
    private String sanitizar(String valor) {
        if (valor == null || valor.isBlank() || valor.length() > 64 || !valor.matches("[A-Za-z0-9._-]+")) {
            return null;
        }
        return valor;
    }
}
