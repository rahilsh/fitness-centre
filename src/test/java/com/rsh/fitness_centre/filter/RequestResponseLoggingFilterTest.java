package com.rsh.fitness_centre.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Vector;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for RequestResponseLoggingFilter.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("RequestResponseLoggingFilter Tests")
class RequestResponseLoggingFilterTest {

  private RequestResponseLoggingFilter filter;

  @Mock
  private FilterChain filterChain;

  @BeforeEach
  void setUp() {
    filter = new RequestResponseLoggingFilter();
  }

  @Test
  @DisplayName("Should filter GET request")
  void testFilterGETRequest() throws ServletException, IOException {
    MockHttpServletRequest mockRequest = new MockHttpServletRequest("GET", "/api/users");
    MockHttpServletResponse mockResponse = new MockHttpServletResponse();

    filter.doFilterInternal(mockRequest, mockResponse, filterChain);

    verify(filterChain, times(1)).doFilter(any(ContentCachingRequestWrapper.class), any(ContentCachingResponseWrapper.class));
  }

  @Test
  @DisplayName("Should filter POST request with body")
  void testFilterPOSTRequest() throws ServletException, IOException {
    MockHttpServletRequest mockRequest = new MockHttpServletRequest("POST", "/api/users");
    mockRequest.setContent("{\"name\":\"John\"}".getBytes());
    MockHttpServletResponse mockResponse = new MockHttpServletResponse();

    filter.doFilterInternal(mockRequest, mockResponse, filterChain);

    verify(filterChain, times(1)).doFilter(any(ContentCachingRequestWrapper.class), any(ContentCachingResponseWrapper.class));
  }

  @Test
  @DisplayName("Should filter PUT request with body")
  void testFilterPUTRequest() throws ServletException, IOException {
    MockHttpServletRequest mockRequest = new MockHttpServletRequest("PUT", "/api/users/1");
    mockRequest.setContent("{\"name\":\"Jane\"}".getBytes());
    MockHttpServletResponse mockResponse = new MockHttpServletResponse();

    filter.doFilterInternal(mockRequest, mockResponse, filterChain);

    verify(filterChain, times(1)).doFilter(any(ContentCachingRequestWrapper.class), any(ContentCachingResponseWrapper.class));
  }

  @Test
  @DisplayName("Should filter PATCH request with body")
  void testFilterPATCHRequest() throws ServletException, IOException {
    MockHttpServletRequest mockRequest = new MockHttpServletRequest("PATCH", "/api/users/1");
    mockRequest.setContent("{\"name\":\"Jane\"}".getBytes());
    MockHttpServletResponse mockResponse = new MockHttpServletResponse();

    filter.doFilterInternal(mockRequest, mockResponse, filterChain);

    verify(filterChain, times(1)).doFilter(any(ContentCachingRequestWrapper.class), any(ContentCachingResponseWrapper.class));
  }

  @Test
  @DisplayName("Should filter DELETE request")
  void testFilterDELETERequest() throws ServletException, IOException {
    MockHttpServletRequest mockRequest = new MockHttpServletRequest("DELETE", "/api/users/1");
    MockHttpServletResponse mockResponse = new MockHttpServletResponse();

    filter.doFilterInternal(mockRequest, mockResponse, filterChain);

    verify(filterChain, times(1)).doFilter(any(ContentCachingRequestWrapper.class), any(ContentCachingResponseWrapper.class));
  }

  @Test
  @DisplayName("Should skip filter for /swagger-ui requests")
  void testSkipFilterForSwaggerUI() {
    MockHttpServletRequest mockRequest = new MockHttpServletRequest("GET", "/swagger-ui/index.html");

    boolean shouldNotFilter = filter.shouldNotFilter(mockRequest);

    assertTrue(shouldNotFilter);
  }

  @Test
  @DisplayName("Should skip filter for /v3/api-docs requests")
  void testSkipFilterForApiDocs() {
    MockHttpServletRequest mockRequest = new MockHttpServletRequest("GET", "/v3/api-docs");

    boolean shouldNotFilter = filter.shouldNotFilter(mockRequest);

    assertTrue(shouldNotFilter);
  }

  @Test
  @DisplayName("Should skip filter for /webjars requests")
  void testSkipFilterForWebjars() {
    MockHttpServletRequest mockRequest = new MockHttpServletRequest("GET", "/webjars/swagger-ui/swagger-ui.css");

    boolean shouldNotFilter = filter.shouldNotFilter(mockRequest);

    assertTrue(shouldNotFilter);
  }

  @Test
  @DisplayName("Should skip filter for /health endpoint")
  void testSkipFilterForHealthEndpoint() {
    MockHttpServletRequest mockRequest = new MockHttpServletRequest("GET", "/health");

    boolean shouldNotFilter = filter.shouldNotFilter(mockRequest);

    assertTrue(shouldNotFilter);
  }

  @Test
  @DisplayName("Should not skip filter for normal API requests")
  void testNotSkipFilterForNormalRequests() {
    MockHttpServletRequest mockRequest = new MockHttpServletRequest("GET", "/api/users");

    boolean shouldNotFilter = filter.shouldNotFilter(mockRequest);

    assertFalse(shouldNotFilter);
  }

  @Test
  @DisplayName("Should handle exceptions and continue filter chain")
  void testExceptionHandling() throws ServletException, IOException {
    MockHttpServletRequest mockRequest = new MockHttpServletRequest("GET", "/api/users");
    MockHttpServletResponse mockResponse = new MockHttpServletResponse();
    doThrow(new ServletException("Test exception")).when(filterChain).doFilter(any(), any());

    assertThrows(ServletException.class, () -> {
      filter.doFilterInternal(mockRequest, mockResponse, filterChain);
    });

    verify(filterChain, times(1)).doFilter(any(ContentCachingRequestWrapper.class), any(ContentCachingResponseWrapper.class));
  }

  @Test
  @DisplayName("Should mask sensitive headers")
  void testSensitiveHeaderMasking() throws ServletException, IOException {
    MockHttpServletRequest mockRequest = new MockHttpServletRequest("GET", "/api/users");
    mockRequest.addHeader("Authorization", "Bearer token123");
    mockRequest.addHeader("Content-Type", "application/json");
    mockRequest.addHeader("X-API-Key", "secret-key");
    mockRequest.addHeader("Password", "SecurePassword123");
    MockHttpServletResponse mockResponse = new MockHttpServletResponse();

    filter.doFilterInternal(mockRequest, mockResponse, filterChain);

    verify(filterChain, times(1)).doFilter(any(ContentCachingRequestWrapper.class), any(ContentCachingResponseWrapper.class));
  }

  @Test
  @DisplayName("Should handle request with query parameters")
  void testRequestWithQueryParameters() throws ServletException, IOException {
    MockHttpServletRequest mockRequest = new MockHttpServletRequest("GET", "/api/users");
    mockRequest.setQueryString("page=1&limit=10");
    MockHttpServletResponse mockResponse = new MockHttpServletResponse();

    filter.doFilterInternal(mockRequest, mockResponse, filterChain);

    verify(filterChain, times(1)).doFilter(any(ContentCachingRequestWrapper.class), any(ContentCachingResponseWrapper.class));
  }

  @Test
  @DisplayName("Should handle empty request body")
  void testEmptyRequestBody() throws ServletException, IOException {
    MockHttpServletRequest mockRequest = new MockHttpServletRequest("POST", "/api/users");
    mockRequest.setContent(new byte[0]);
    MockHttpServletResponse mockResponse = new MockHttpServletResponse();

    filter.doFilterInternal(mockRequest, mockResponse, filterChain);

    verify(filterChain, times(1)).doFilter(any(ContentCachingRequestWrapper.class), any(ContentCachingResponseWrapper.class));
  }
}
