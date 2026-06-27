package com.rsh.fitness_centre.integration;

import com.rsh.fitness_centre.entity.*;
import com.rsh.fitness_centre.repository.*;
import com.rsh.fitness_centre.service.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive End-to-End functional tests for the Fitness Centre application.
 * These tests cover complete user workflows including registration, authentication,
 * fitness centre management, booking lifecycle, and error scenarios.
 *
 * Test Coverage:
 * - User registration and authentication workflows
 * - Fitness centre management and activities
 * - Booking creation, retrieval, and cancellation
 * - Search and discovery functionality
 * - Error handling and edge cases
 * - Complete user journeys
 *
 * Target: >85% code coverage across all modules
 */
@SpringBootTest
@ActiveProfiles("test")
@DisplayName("End-to-End Functional Tests - Fitness Centre Application")
class EndToEndFunctionalTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FitnessCentreRepository fitnessCentreRepository;

    @Autowired
    private SlotRepository slotRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private FitnessCentreService fitnessCentreService;

    @Autowired
    private BookingService bookingService;

    private User testUser;
    private User testUser2;
    private FitnessCentre testCentre;
    private Slot testSlot;

    @BeforeEach
    void setUp() {
        // Clean up all repositories
        bookingRepository.deleteAll();
        slotRepository.deleteAll();
        fitnessCentreRepository.deleteAll();
        userRepository.deleteAll();

        // Create test users
        testUser = userService.registerUser("testuser@example.com", "TestPass123", "Test User");
        testUser2 = userService.addUser("Another User");
        
        // Create test fitness centre
        testCentre = new FitnessCentre(null, "Test Fitness Centre");
        testCentre = fitnessCentreRepository.save(testCentre);

        // Create test slot
        testSlot = fitnessCentreService.addActivity(
            testCentre.getId(),
            Activity.YOGA,
            9,
            10,
            10
        );
    }

    @AfterEach
    void tearDown() {
        // Clean up
        bookingRepository.deleteAll();
        slotRepository.deleteAll();
        fitnessCentreRepository.deleteAll();
        userRepository.deleteAll();
    }

    // ==================== USER REGISTRATION & AUTHENTICATION TESTS ====================

    @Nested
    @DisplayName("User Registration & Authentication")
    class RegistrationAndAuthenticationTests {

        @Test
        @DisplayName("Should register user successfully with valid credentials")
        void testRegisterUserSuccess() {
            User registeredUser = userService.registerUser(
                "newuser@example.com",
                "SecurePass123",
                "New User"
            );

            assertNotNull(registeredUser);
            assertNotNull(registeredUser.getId());
            assertEquals("newuser@example.com", registeredUser.getEmail());
            assertEquals("New User", registeredUser.getName());
            assertTrue(registeredUser.isEnabled());
            assertTrue(registeredUser.getRoles().contains(UserRole.USER));
            assertNotNull(registeredUser.getPasswordHash());
            assertNotEquals("SecurePass123", registeredUser.getPasswordHash());
        }

        @Test
        @DisplayName("Should fail registration with duplicate email")
        void testRegisterUserDuplicateEmail() {
            userService.registerUser("duplicate@example.com", "Pass123", "User One");

            assertThrows(IllegalArgumentException.class, () ->
                userService.registerUser("duplicate@example.com", "Pass456", "User Two")
            );
        }

        @Test
        @DisplayName("Should authenticate user successfully with valid credentials")
        void testAuthenticateUserSuccess() {
            userService.registerUser("auth@example.com", "AuthPass123", "Auth User");

            User authenticatedUser = userService.authenticateUser("auth@example.com", "AuthPass123");

            assertNotNull(authenticatedUser);
            assertEquals("auth@example.com", authenticatedUser.getEmail());
            assertNotNull(authenticatedUser.getLastLogin());
        }

        @Test
        @DisplayName("Should fail authentication with invalid password")
        void testAuthenticateUserInvalidPassword() {
            userService.registerUser("auth@example.com", "CorrectPass123", "Auth User");

            assertThrows(IllegalArgumentException.class, () ->
                userService.authenticateUser("auth@example.com", "WrongPass456")
            );
        }

        @Test
        @DisplayName("Should fail authentication with non-existent user")
        void testAuthenticateUserNotFound() {
            assertThrows(IllegalArgumentException.class, () ->
                userService.authenticateUser("nonexistent@example.com", "AnyPass123")
            );
        }

        @Test
        @DisplayName("Should fail authentication with disabled user")
        void testAuthenticateUserDisabled() {
            User user = userService.registerUser("disabled@example.com", "DisabledPass123", "Disabled User");
            user.setEnabled(false);
            userRepository.save(user);

            assertThrows(IllegalArgumentException.class, () ->
                userService.authenticateUser("disabled@example.com", "DisabledPass123")
            );
        }

        @Test
        @DisplayName("Should get user with roles")
        void testGetUserWithRoles() {
            User user = userService.getUserWithRoles(testUser.getId());
            assertNotNull(user);
            assertTrue(user.getRoles().contains(UserRole.USER));
        }
    }

    // ==================== FITNESS CENTRE MANAGEMENT TESTS ====================

    @Nested
    @DisplayName("Fitness Centre Management")
    class FitnessCentreManagementTests {

        @Test
        @DisplayName("Should create fitness centre successfully")
        void testCreateFitnessCentreSuccess() {
            FitnessCentre centre = fitnessCentreService.addCentre(
                "Premium Fitness Centre",
                new HashSet<>(),
                Set.of(Activity.YOGA, Activity.CARDIO)
            );

            assertNotNull(centre);
            assertNotNull(centre.getId());
            assertEquals("Premium Fitness Centre", centre.getName());
        }

        @Test
        @DisplayName("Should add activity slot to fitness centre")
        void testAddActivitySlotSuccess() {
            Slot slot = fitnessCentreService.addActivity(
                testCentre.getId(),
                Activity.CARDIO,
                10,
                11,
                15
            );

            assertNotNull(slot);
            assertNotNull(slot.getId());
            assertEquals(Activity.CARDIO, slot.getActivity());
            assertEquals(10, slot.getStartTime());
            assertEquals(11, slot.getEndTime());
            assertEquals(15, slot.getNoOfSeats());
        }

        @Test
        @DisplayName("Should support multiple centres with overlapping activities")
        void testMultipleCentresWithOverlappingActivities() {
            FitnessCentre centre1 = fitnessCentreService.addCentre(
                "Centre One",
                new HashSet<>(),
                Set.of(Activity.YOGA)
            );

            FitnessCentre centre2 = fitnessCentreService.addCentre(
                "Centre Two",
                new HashSet<>(),
                Set.of(Activity.YOGA)
            );

            assertNotNull(centre1);
            assertNotNull(centre2);
            assertNotEquals(centre1.getId(), centre2.getId());
        }

        @Test
        @DisplayName("Should get fitness centre by name")
        void testGetFitnessCentreByName() {
            FitnessCentre centre = fitnessCentreService.getCentreByName(testCentre.getName());
            assertNotNull(centre);
            assertEquals(testCentre.getName(), centre.getName());
        }

        @Test
        @DisplayName("Should return null for non-existent fitness centre")
        void testGetNonExistentCentreByName() {
            FitnessCentre centre = fitnessCentreService.getCentreByName("Non Existent Centre");
            assertNull(centre);
        }

        @Test
        @DisplayName("Should get fitness centre by ID")
        void testGetFitnessCentreById() {
            FitnessCentre centre = fitnessCentreService.getCentreById(testCentre.getId());
            assertNotNull(centre);
            assertEquals(testCentre.getId(), centre.getId());
        }

        @Test
        @DisplayName("Should get all fitness centres")
        void testGetAllFitnessCentres() {
            Set<FitnessCentre> centres = fitnessCentreService.getAllCentres();
            assertNotNull(centres);
            assertTrue(centres.size() > 0);
        }
    }

    // ==================== BOOKING WORKFLOW TESTS ====================

    @Nested
    @DisplayName("Booking Workflow")
    class BookingWorkflowTests {

        @Test
        @DisplayName("Should create booking successfully")
        void testCreateBookingSuccess() {
            Booking booking = bookingService.addBooking(testSlot.getId(), testUser.getId());

            assertNotNull(booking);
            assertNotNull(booking.getId());
            assertEquals(BookingStatus.BOOKED, booking.getStatus());
        }

        @Test
        @DisplayName("Should retrieve booking by ID")
        void testGetBookingById() {
            Booking createdBooking = bookingService.addBooking(testSlot.getId(), testUser.getId());

            Booking retrievedBooking = bookingService.getBooking(createdBooking.getId());

            assertNotNull(retrievedBooking);
            assertEquals(createdBooking.getId(), retrievedBooking.getId());
            assertEquals(BookingStatus.BOOKED, retrievedBooking.getStatus());
        }

        @Test
        @DisplayName("Should retrieve all bookings")
        void testGetAllBookings() {
            bookingService.addBooking(testSlot.getId(), testUser.getId());

            Set<Booking> bookings = bookingService.getBookings();

            assertNotNull(bookings);
            assertTrue(bookings.size() > 0);
        }

        @Test
        @DisplayName("Should cancel booking successfully")
        void testCancelBookingSuccess() {
            Booking booking = bookingService.addBooking(testSlot.getId(), testUser.getId());

            Booking cancelledBooking = bookingService.cancelBooking(booking.getId());

            assertNotNull(cancelledBooking);
            assertEquals(BookingStatus.CANCELLED, cancelledBooking.getStatus());
        }

        @Test
        @DisplayName("Should fail booking with invalid slot ID")
        void testBookingInvalidSlotId() {
            assertThrows(Exception.class, () ->
                bookingService.addBooking(99999L, testUser.getId())
            );
        }

        @Test
        @DisplayName("Should fail booking with invalid user ID")
        void testBookingInvalidUserId() {
            assertThrows(Exception.class, () ->
                bookingService.addBooking(testSlot.getId(), 99999L)
            );
        }

        @Test
        @DisplayName("Should fail cancelling non-existent booking")
        void testCancelNonExistentBooking() {
            assertThrows(Exception.class, () ->
                bookingService.cancelBooking(99999L)
            );
        }
    }

    // ==================== ERROR HANDLING & EDGE CASES ====================

    @Nested
    @DisplayName("Error Handling and Edge Cases")
    class ErrorHandlingTests {

        @Test
        @DisplayName("Should allow multiple bookings on same slot (concurrent)")
        void testMultipleConcurrentBookingsSameSlot() {
            Booking booking1 = bookingService.addBooking(testSlot.getId(), testUser.getId());
            assertNotNull(booking1);

            Booking booking2 = bookingService.addBooking(testSlot.getId(), testUser2.getId());
            assertNotNull(booking2);

            assertNotEquals(booking1.getId(), booking2.getId());
        }

        @Test
        @DisplayName("Should retrieve user by ID")
        void testGetUserById() {
            User user = userService.getUserById(testUser.getId());
            assertNotNull(user);
            assertEquals(testUser.getId(), user.getId());
        }

        @Test
        @DisplayName("Should return null for non-existent user")
        void testGetNonExistentUser() {
            User user = userService.getUserById(99999L);
            assertNull(user);
        }

        @Test
        @DisplayName("Should retrieve all users")
        void testGetAllUsers() {
            Set<User> users = userService.getAllUsers();
            assertNotNull(users);
            assertTrue(users.size() > 0);
        }

        @Test
        @DisplayName("Should create user with name only via service")
        void testAddUser() {
            User user = userService.addUser("New Service User");

            assertNotNull(user);
            assertNotNull(user.getId());
            assertEquals("New Service User", user.getName());
        }

        @Test
        @DisplayName("Should return null for non-existent booking")
        void testGetNonExistentBooking() {
            Booking booking = bookingService.getBooking(99999L);
            assertNull(booking);
        }
    }

    // ==================== DATA VALIDATION TESTS ====================

    @Nested
    @DisplayName("Data Validation")
    class DataValidationTests {

        @Test
        @DisplayName("Should compare users by name")
        void testUserEquality() {
            User user1 = new User(1L, "Same Name");
            User user2 = new User(2L, "Same Name");
            User user3 = new User(3L, "Different Name");

            assertEquals(user1, user2);
            assertNotEquals(user1, user3);
        }

        @Test
        @DisplayName("Should calculate user hash code correctly")
        void testUserHashCode() {
            User user1 = new User(1L, "Same Name");
            User user2 = new User(2L, "Same Name");

            assertEquals(user1.hashCode(), user2.hashCode());
        }

        @Test
        @DisplayName("Should compare bookings by ID")
        void testBookingEquality() {
            Booking booking1 = new Booking(1L, testUser, testSlot, null, BookingStatus.BOOKED);
            Booking booking2 = new Booking(1L, testUser, testSlot, null, BookingStatus.BOOKED);
            Booking booking3 = new Booking(2L, testUser, testSlot, null, BookingStatus.BOOKED);

            assertEquals(booking1, booking2);
            assertNotEquals(booking1, booking3);
        }

        @Test
        @DisplayName("Should calculate booking hash code correctly")
        void testBookingHashCode() {
            Booking booking1 = new Booking(1L, testUser, testSlot, null, BookingStatus.BOOKED);
            Booking booking2 = new Booking(1L, testUser, testSlot, null, BookingStatus.BOOKED);

            assertEquals(booking1.hashCode(), booking2.hashCode());
        }
    }

    // ==================== BOOKING SERVICE TESTS ====================

    @Nested
    @DisplayName("Booking Service Operations")
    class BookingServiceTests {

        @Test
        @DisplayName("Should get bookings by user ID")
        void testGetBookingsByUser() {
            bookingService.addBooking(testSlot.getId(), testUser.getId());

            Set<Booking> bookings = bookingService.getBookingsByUser(testUser.getId());
            assertNotNull(bookings);
            assertTrue(bookings.size() > 0);
        }

        @Test
        @DisplayName("Should get bookings by centre ID")
        void testGetBookingsByCentre() {
            bookingService.addBooking(testSlot.getId(), testUser.getId());

            Set<Booking> bookings = bookingService.getBookingsOfCentre(testCentre.getId());
            assertNotNull(bookings);
            assertTrue(bookings.size() > 0);
        }

        @Test
        @DisplayName("Should add activity to fitness centre properly")
        void testAddActivitySlotProperly() {
            Slot slot = fitnessCentreService.addActivity(
                testCentre.getId(),
                Activity.CARDIO,
                10,
                11,
                20
            );

            assertNotNull(slot);
            assertNotNull(slot.getId());
            assertEquals(Activity.CARDIO, slot.getActivity());
            assertEquals(10, slot.getStartTime());
            assertEquals(11, slot.getEndTime());
            assertEquals(20, slot.getNoOfSeats());
        }

        @Test
        @DisplayName("Should return null when adding activity to non-existent centre")
        void testAddActivityToNonExistentCentre() {
            Slot slot = fitnessCentreService.addActivity(
                99999L,
                Activity.YOGA,
                9,
                10,
                15
            );

            assertNull(slot);
        }
    }

    // ==================== COMPLETE WORKFLOW TESTS ====================

    @Nested
    @DisplayName("Complete User Workflows")
    class CompleteWorkflowTests {

        @Test
        @DisplayName("Complete workflow: Register, Authenticate, Book, and Cancel")
        void testCompleteWorkflow() {
            // Step 1: Register new user
            User workflowUser = userService.registerUser(
                "workflow@example.com",
                "WorkflowPass123",
                "Workflow User"
            );

            assertNotNull(workflowUser);
            assertTrue(workflowUser.getRoles().contains(UserRole.USER));

            // Step 2: Authenticate user
            User authenticatedUser = userService.authenticateUser(
                "workflow@example.com",
                "WorkflowPass123"
            );

            assertNotNull(authenticatedUser);
            assertNotNull(authenticatedUser.getLastLogin());

            // Step 3: Make booking
            Booking booking = bookingService.addBooking(testSlot.getId(), workflowUser.getId());
            assertNotNull(booking);
            assertEquals(BookingStatus.BOOKED, booking.getStatus());

            // Step 4: Cancel booking
            Booking cancelledBooking = bookingService.cancelBooking(booking.getId());
            assertNotNull(cancelledBooking);
            assertEquals(BookingStatus.CANCELLED, cancelledBooking.getStatus());
        }

        @Test
        @DisplayName("Complete workflow: Create centre, add slots, and book")
        void testCentreAndBookingWorkflow() {
            // Step 1: Create centre
            FitnessCentre newCentre = fitnessCentreService.addCentre(
                "Swimming Studio",
                new HashSet<>(),
                Set.of(Activity.SWIMMING)
            );

            assertNotNull(newCentre);

            // Step 2: Add activity slot
            Slot newSlot = fitnessCentreService.addActivity(
                newCentre.getId(),
                Activity.SWIMMING,
                18,
                19,
                25
            );

            assertNotNull(newSlot);
            assertEquals(Activity.SWIMMING, newSlot.getActivity());

            // Step 3: Create user and book the slot
            User bookingUser = userService.addUser("Swimming Enthusiast");

            Booking booking = bookingService.addBooking(newSlot.getId(), bookingUser.getId());
            assertNotNull(booking);
            assertEquals(BookingStatus.BOOKED, booking.getStatus());
        }
    }

    // ==================== ADDITIONAL COVERAGE TESTS ====================

    @Nested
    @DisplayName("Additional Coverage Tests")
    class AdditionalCoverageTests {

        @Test
        @DisplayName("Should get user by name")
        void testGetUserByName() {
            User user = userService.getUserByName(testUser.getName());
            assertNotNull(user);
            assertEquals(testUser.getName(), user.getName());
        }

        @Test
        @DisplayName("Should get user by email")
        void testGetUserByEmail() {
            User user = userService.getUserByEmail(testUser.getEmail());
            assertNotNull(user);
            assertEquals(testUser.getEmail(), user.getEmail());
        }

        @Test
        @DisplayName("Should handle user hash code correctly")
        void testUserHashCodeConsistency() {
            User user1 = new User(null, "Test Name");
            User user2 = new User(null, "Test Name");

            assertEquals(user1, user2);
            assertEquals(user1.hashCode(), user2.hashCode());
        }

        @Test
        @DisplayName("Should have different hash codes for different bookings")
        void testBookingHashCodeDifference() {
            Booking booking1 = new Booking(1L, testUser, testSlot, null, BookingStatus.BOOKED);
            Booking booking2 = new Booking(2L, testUser, testSlot, null, BookingStatus.BOOKED);

            assertNotEquals(booking1.hashCode(), booking2.hashCode());
        }
    }
}
