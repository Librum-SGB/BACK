package com.sgb.mylibrum.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

class RequestLoggingFilterTest {

    private final RequestLoggingFilter filter = new RequestLoggingFilter();

    @AfterEach
    void limparMdc() {
        MDC.clear();
    }

    @Test
    void devePropagarRequestIdSeguroENaoDeixarMdcEntreRequisicoes() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("GET");
        request.setRequestURI("/api/materiais");
        request.addHeader(RequestLoggingFilter.HEADER_REQUEST_ID, "req-123.abc");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        filter.doFilter(request, response, chain);

        assertEquals("req-123.abc", response.getHeader(RequestLoggingFilter.HEADER_REQUEST_ID));
        assertTrue(chain.getRequest() == request);
        assertNull(MDC.get(RequestLoggingFilter.MDC_REQUEST_ID));
    }

    @Test
    void deveSubstituirRequestIdComCaracteresInseguros() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/usuarios");
        request.addHeader(RequestLoggingFilter.HEADER_REQUEST_ID, "id\r\nforjado");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, new MockFilterChain());

        String requestId = response.getHeader(RequestLoggingFilter.HEADER_REQUEST_ID);
        assertTrue(requestId.matches("[A-Za-z0-9._-]{8}"));
    }

    @Test
    void naoDeveAdicionarRequestIdAoHealthCheck() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/actuator/health");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        filter.doFilter(request, response, chain);

        assertEquals(request, chain.getRequest());
        assertNull(response.getHeader(RequestLoggingFilter.HEADER_REQUEST_ID));
    }
}
