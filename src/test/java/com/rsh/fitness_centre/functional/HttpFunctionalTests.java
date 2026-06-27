package com.rsh.fitness_centre.functional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rsh.fitness_centre.entity.Activity;
import com.rsh.fitness_centre.entity.User;
import com.rsh.fitness_centre.entity.UserRole;
import com.rsh.fitness_centre.entity.request.LoginRequest;
import com.rsh.fitness_centre.entity.request.RegisterRequest;
import com.rsh.fitness_centre.entity.request.AddUserRequest;
import com.rsh.fitness_centre.entity.request.AddFitnessCentreRequest;
import com.rsh.fitness_centre.entity.request.AddActivityRequest;
import com.rsh.fitness_centre.entity.request.AddBookingRequest;
import com.rsh.fitness_centre.entity.request.SearchActivityRequest;
import com.rsh.fitness_centre.repository.BookingRepository;
import com.rsh.fitness_centre.repository.FitnessCentreRepository;
import com.rsh.fitness_centre.repository.SlotRepository;
import com.rsh.fitness_centre.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * End-to-End HTTP Functional Tests using standard Java 21 HttpClient
 * 
 * These tests boot up a real embedded servlet container (Tomcat) on a RANDOM PORT
 * and make real network loopback HTTP requests using the built-in JDK 21 HttpClient.
 * 
 * This tests the complete HTTP stack end-to-end:
 * - Actual HTTP routing and dispatching
 * - Real Jackson serialization/deserialization over HTTP
 * - Real Spring Security JWT Filter chain
 * - Real Global Exception Handler mapping exceptions to HTTP statuses
 * - Real Input Validation (@Valid)
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DisplayName("E2E HTTP Functional Tests (Real Embedded Server)")
public class HttpFunctionalTests {

    @LocalServerPort
    private int port;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FitnessCentreRepository fitnessCentreRepository;

    @Autowired
    private SlotRepository slotRepository;

    @Autowired
    private BookingRepository bookingRepository;

    private HttpClient httpClient;
    private ObjectMapper objectMapper;
    private String validJwt;

    private String getBaseUrl() {
        return "http://localhost:" + port;
    }

    @org.junit.jupiter.api.AfterEach
    void tearDown() {
        if (httpClient != null) {
            httpClient.close();
        }
    }

    @BeforeEach
    void setUp() throws Exception {
        httpClient = HttpClient.newHttpClient();
        objectMapper = new ObjectMapper();

        // Clean up database tables in order of dependencies
        bookingRepository.deleteAll();
        slotRepository.deleteAll();
        fitnessCentreRepository.deleteAll();
        userRepository.deleteAll();

        // 1. HTTP POST /auth/register
        RegisterRequest registerReq = new RegisterRequest();
        registerReq.setEmail("testuser@example.com");
        registerReq.setName("Test User");
        registerReq.setPassword("TestPass123");
        registerReq.setPasswordConfirmation("TestPass123");

        HttpRequest registerHttpReq = HttpRequest.newBuilder()
                .uri(URI.create(getBaseUrl() + "/auth/register"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(registerReq)))
                .build();

        HttpResponse<String> registerResponse = httpClient.send(registerHttpReq, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, registerResponse.statusCode());

        // Make the registered user an ADMIN in database so they can access restricted endpoints
        User registeredUser = userRepository.findByEmail("testuser@example.com").orElseThrow();
        registeredUser.setRoles(new HashSet<>(Arrays.asList(UserRole.USER, UserRole.ADMIN, UserRole.SUPER_ADMIN)));
        userRepository.save(registeredUser);

        // 2. HTTP POST /auth/login to get token
        LoginRequest loginReq = new LoginRequest();
        loginReq.setEmail("testuser@example.com");
        loginReq.setPassword("TestPass123");

        HttpRequest loginHttpReq = HttpRequest.newBuilder()
                .uri(URI.create(getBaseUrl() + "/auth/login"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(loginReq)))
                .build();

        HttpResponse<String> loginResponse = httpClient.send(loginHttpReq, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, loginResponse.statusCode());
        
        Map<?, ?> responseBody = objectMapper.readValue(loginResponse.body(), Map.class);
        validJwt = (String) responseBody.get("token");
        assertNotNull(validJwt);
    }

    // Helper to send authorized requests
    private HttpResponse<String> sendAuthorizedRequest(String method, String path, Object body) throws Exception {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(getBaseUrl() + path))
                .header("Authorization", validJwt)
                .header("Content-Type", "application/json");

        if (body != null) {
            String json = objectMapper.writeValueAsString(body);
            builder.method(method, HttpRequest.BodyPublishers.ofString(json));
        } else {
            builder.method(method, HttpRequest.BodyPublishers.noBody());
        }

        return httpClient.send(builder.build(), HttpResponse.BodyHandlers.ofString());
    }

    // ==================== PUBLIC ENDPOINTS ====================

    @Test
    @DisplayName("GET /health returns 200 OK")
    void testHealthCheck() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(getBaseUrl() + "/health"))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals("Healthy!", response.body());
    }

    @Test
    @DisplayName("GET /search is accessible without authentication")
    void testSearchWithoutAuth() throws Exception {
        SearchActivityRequest searchReq = new SearchActivityRequest();
        searchReq.setActivity("YOGA");
        searchReq.setFitnessCentreName("");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(getBaseUrl() + "/search"))
                .header("Content-Type", "application/json")
                .method("GET", HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(searchReq)))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        List<?> body = objectMapper.readValue(response.body(), List.class);
        assertNotNull(body);
    }

    // ==================== AUTHENTICATION ENDPOINTS ====================

    @Test
    @DisplayName("POST /auth/register with invalid email returns 400 Bad Request")
    void testRegisterWithInvalidEmail() throws Exception {
        RegisterRequest badReq = new RegisterRequest();
        badReq.setEmail("invalid-email");
        badReq.setName("Bad User");
        badReq.setPassword("TestPass123");
        badReq.setPasswordConfirmation("TestPass123");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(getBaseUrl() + "/auth/register"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(badReq)))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode());
        Map<?, ?> body = objectMapper.readValue(response.body(), Map.class);
        assertNotNull(body.get("error"));
    }

    @Test
    @DisplayName("POST /auth/login with invalid credentials returns 401 Unauthorized")
    void testLoginWithInvalidCredentials() throws Exception {
        LoginRequest badReq = new LoginRequest();
        badReq.setEmail("testuser@example.com");
        badReq.setPassword("WrongPassword123");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(getBaseUrl() + "/auth/login"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(badReq)))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(401, response.statusCode());
        
        // Only parse if body is not empty
        if (response.body() != null && !response.body().trim().isEmpty()) {
            Map<?, ?> body = objectMapper.readValue(response.body(), Map.class);
            assertNotNull(body.get("error"));
        }
    }

    @Test
    @DisplayName("GET /auth/me returns 401 or 403 without auth header")
    void testGetMeWithoutAuth() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(getBaseUrl() + "/auth/me"))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertTrue(response.statusCode() == 401 || response.statusCode() == 403);
    }

    @Test
    @DisplayName("GET /auth/me with valid Bearer token returns 200 OK")
    void testGetMeWithAuth() throws Exception {
        HttpResponse<String> response = sendAuthorizedRequest("GET", "/auth/me", null);
        assertEquals(200, response.statusCode());
        Map<?, ?> body = objectMapper.readValue(response.body(), Map.class);
        assertEquals("testuser@example.com", body.get("email"));
    }

    // ==================== USER MANAGEMENT ====================

    @Test
    @DisplayName("GET /users requires authentication")
    void testGetUsersRequiresAuth() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(getBaseUrl() + "/users"))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertTrue(response.statusCode() == 401 || response.statusCode() == 403);
    }

    @Test
    @DisplayName("POST /users and GET /users end-to-end HTTP lifecycle")
    void testUserLifecycle() throws Exception {
        AddUserRequest userReq = new AddUserRequest();
        userReq.setName("New User Member");

        // Create user
        HttpResponse<String> createResponse = sendAuthorizedRequest("POST", "/users", userReq);
        assertEquals(201, createResponse.statusCode());
        Map<?, ?> createBody = objectMapper.readValue(createResponse.body(), Map.class);
        Integer userId = (Integer) createBody.get("id");
        assertNotNull(userId);

        // Fetch users list
        HttpResponse<String> listResponse = sendAuthorizedRequest("GET", "/users", null);
        assertEquals(200, listResponse.statusCode());
        Map<?, ?> pageBody = objectMapper.readValue(listResponse.body(), Map.class);
        List<?> listBody = (List<?>) pageBody.get("content");
        assertTrue(listBody.size() >= 2); // includes registered testuser
    }

    // ==================== FITNESS CENTRE END-TO-END HTTP TESTS ====================

    @Test
    @DisplayName("POST /fitnessCentres with invalid structure returns 400 Bad Request")
    void testCreateCentreWithInvalidPayload() throws Exception {
        AddFitnessCentreRequest badReq = new AddFitnessCentreRequest();
        badReq.setName(""); // triggers validation error

        HttpResponse<String> response = sendAuthorizedRequest("POST", "/fitnessCentres", badReq);
        assertEquals(400, response.statusCode());
    }

    @Test
    @DisplayName("E2E Fitness Centre, Slots, and Booking flow over HTTP")
    void testE2eCentreAndBookingFlow() throws Exception {
        // 1. Create Fitness Centre
        AddFitnessCentreRequest centreReq = new AddFitnessCentreRequest();
        centreReq.setName("E2E Power Gym");
        centreReq.setTimings(Set.of(List.of(6, 22)));
        centreReq.setSupportedActivities(Set.of("YOGA", "CARDIO"));

        HttpResponse<String> centreResponse = sendAuthorizedRequest("POST", "/fitnessCentres", centreReq);
        assertEquals(201, centreResponse.statusCode());
        Map<?, ?> centreBody = objectMapper.readValue(centreResponse.body(), Map.class);
        Integer centreId = (Integer) centreBody.get("id");
        assertNotNull(centreId);

        // 2. Add Activity Slot (YOGA, 9am to 10am)
        AddActivityRequest slotReq = new AddActivityRequest();
        slotReq.setActivity(Activity.YOGA);
        slotReq.setStartTime(9);
        slotReq.setEndTime(10);
        slotReq.setNoOfSlots(15);

        HttpResponse<String> slotResponse = sendAuthorizedRequest("POST", "/fitnessCentres/" + centreId + "/slots", slotReq);
        assertEquals(201, slotResponse.statusCode());
        Map<?, ?> slotBody = objectMapper.readValue(slotResponse.body(), Map.class);
        Integer slotId = (Integer) slotBody.get("id");
        assertNotNull(slotId);

        // 3. Register a client member user
        AddUserRequest userReq = new AddUserRequest();
        userReq.setName("Client Gymgoer");
        HttpResponse<String> userResponse = sendAuthorizedRequest("POST", "/users", userReq);
        assertEquals(201, userResponse.statusCode());
        Map<?, ?> userBody = objectMapper.readValue(userResponse.body(), Map.class);
        Integer clientId = (Integer) userBody.get("id");

        // 4. Create Booking
        AddBookingRequest bookingReq = new AddBookingRequest();
        bookingReq.setSlotId(slotId.longValue());
        bookingReq.setUserId(clientId.longValue());

        HttpResponse<String> bookingResponse = sendAuthorizedRequest("POST", "/bookings", bookingReq);
        assertEquals(201, bookingResponse.statusCode());
        Map<?, ?> bookingBody = objectMapper.readValue(bookingResponse.body(), Map.class);
        Integer bookingId = (Integer) bookingBody.get("id");
        assertNotNull(bookingId);

        // 5. Cancel Booking
        HttpResponse<String> cancelResponse = sendAuthorizedRequest("PATCH", "/bookings/" + bookingId, null);
        assertEquals(200, cancelResponse.statusCode());
        Map<?, ?> cancelBody = objectMapper.readValue(cancelResponse.body(), Map.class);
        assertEquals("CANCELLED", cancelBody.get("status"));
    }

    // ==================== HTTP METHOD & ERROR STATUS TESTS ====================

    @Test
    @DisplayName("HTTP DELETE /auth/login returns 405 Method Not Allowed")
    void testInvalidHttpMethod() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(getBaseUrl() + "/auth/login"))
                .DELETE()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(405, response.statusCode());
    }

    @Test
    @DisplayName("GET /nonexistent returns 404 Not Found")
    void testNonExistentEndpoint() throws Exception {
        HttpResponse<String> response = sendAuthorizedRequest("GET", "/nonexistent-resource", null);
        assertEquals(404, response.statusCode());
    }

    @Test
    @DisplayName("Routing URLs are case sensitive (GET /Health returns 404)")
    void testCaseSensitiveUrls() throws Exception {
        HttpResponse<String> response = sendAuthorizedRequest("GET", "/Health", null);
        assertEquals(404, response.statusCode());
    }
}
