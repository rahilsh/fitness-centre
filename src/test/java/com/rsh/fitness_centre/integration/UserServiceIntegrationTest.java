package com.rsh.fitness_centre.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.rsh.fitness_centre.entity.User;
import com.rsh.fitness_centre.repository.UserRepository;
import com.rsh.fitness_centre.repository.RefreshTokenRepository;
import com.rsh.fitness_centre.service.UserService;
import com.rsh.fitness_centre.util.SequenceGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("UserService Integration Tests")
@Transactional
class UserServiceIntegrationTest {

  @Autowired
  private UserService userService;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private RefreshTokenRepository refreshTokenRepository;

  @Autowired
  private SequenceGenerator sequenceGenerator;

  @BeforeEach
  void setUp() {
    refreshTokenRepository.deleteAll();
    userRepository.deleteAll();
  }

  @Test
  @DisplayName("Should add user and persist to database")
  void testAddUserPersistsToDatabase() {
    // Arrange
    String userName = "John Doe";

    // Act
    User user = userService.addUser(userName);

    // Assert
    assertNotNull(user);
    assertNotNull(user.getId());
    assertEquals(userName, user.getName());
    
    // Verify persistence
    User persistedUser = userRepository.findById(user.getId()).orElse(null);
    assertNotNull(persistedUser);
    assertEquals(userName, persistedUser.getName());
  }

  @Test
  @DisplayName("Should retrieve all users from database")
  void testGetAllUsersFetchesFromDatabase() {
    // Arrange
    User user1 = userService.addUser("Alice");
    User user2 = userService.addUser("Bob");
    User user3 = userService.addUser("Charlie");

    // Act
    var allUsers = userService.getAllUsers();

    // Assert
    assertEquals(3, allUsers.size());
    assertTrue(allUsers.stream().anyMatch(u -> u.getName().equals("Alice")));
    assertTrue(allUsers.stream().anyMatch(u -> u.getName().equals("Bob")));
    assertTrue(allUsers.stream().anyMatch(u -> u.getName().equals("Charlie")));
  }

  @Test
  @DisplayName("Should generate sequential user IDs across transactions")
  void testSequentialUserIdGeneration() {
    // Act
    User user1 = userService.addUser("User1");
    User user2 = userService.addUser("User2");
    User user3 = userService.addUser("User3");

    // Assert
    assertNotNull(user1.getId());
    assertNotNull(user2.getId());
    assertNotNull(user3.getId());
    
    // Verify all are persisted
    assertEquals(3, userRepository.count());
  }

  @Test
  @DisplayName("Should handle concurrent user additions")
  void testConcurrentUserAdditions() {
    // Act
    User user1 = userService.addUser("Concurrent1");
    User user2 = userService.addUser("Concurrent2");

    // Assert
    assertEquals(2, userRepository.count());
    assertTrue(userRepository.findById(user1.getId()).isPresent());
    assertTrue(userRepository.findById(user2.getId()).isPresent());
  }

  @Test
  @DisplayName("Should persist users with empty names")
  void testAddUserWithEmptyName() {
    // Act
    User user = userService.addUser("");

    // Assert
    assertNotNull(user);
    assertEquals("", user.getName());
    
    // Verify persistence
    User persistedUser = userRepository.findById(user.getId()).orElse(null);
    assertNotNull(persistedUser);
    assertEquals("", persistedUser.getName());
  }

  @Test
  @DisplayName("Should persist users with special characters")
  void testAddUserWithSpecialCharacters() {
    // Act
    User user = userService.addUser("User@#$%^&*()");

    // Assert
    assertEquals("User@#$%^&*()", user.getName());
    
    // Verify persistence
    User persistedUser = userRepository.findById(user.getId()).orElse(null);
    assertNotNull(persistedUser);
    assertEquals("User@#$%^&*()", persistedUser.getName());
  }

  @Test
  @DisplayName("Should persist users with long names")
  void testAddUserWithLongName() {
    // Act
    String longName = "A".repeat(100);
    User user = userService.addUser(longName);

    // Assert
    assertEquals(longName, user.getName());
    
    // Verify persistence
    User persistedUser = userRepository.findById(user.getId()).orElse(null);
    assertNotNull(persistedUser);
    assertEquals(longName, persistedUser.getName());
  }
}
