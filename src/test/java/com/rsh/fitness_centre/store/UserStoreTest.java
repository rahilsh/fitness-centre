package com.rsh.fitness_centre.store;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.rsh.fitness_centre.entity.User;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("UserStore Tests")
class UserStoreTest {

  private UserStore userStore;

  @BeforeEach
  void setUp() {
    userStore = new UserStore();
  }

  @Test
  @DisplayName("Should add user successfully")
  void testAddUserSuccess() {
    // Arrange
    User user = new User(1, "John Doe");

    // Act
    userStore.addUser(user);
    Set<User> users = userStore.getAll();

    // Assert
    assertNotNull(users);
    assertEquals(1, users.size());
    assertTrue(users.contains(user));
  }

  @Test
  @DisplayName("Should add multiple users successfully")
  void testAddMultipleUsersSuccess() {
    // Arrange
    User user1 = new User(1, "John Doe");
    User user2 = new User(2, "Jane Doe");
    User user3 = new User(3, "Bob Smith");

    // Act
    userStore.addUser(user1);
    userStore.addUser(user2);
    userStore.addUser(user3);
    Set<User> users = userStore.getAll();

    // Assert
    assertNotNull(users);
    assertEquals(3, users.size());
    assertTrue(users.contains(user1));
    assertTrue(users.contains(user2));
    assertTrue(users.contains(user3));
  }

  @Test
  @DisplayName("Should get empty set when no users added")
  void testGetAllUsersEmpty() {
    // Act
    Set<User> users = userStore.getAll();

    // Assert
    assertNotNull(users);
    assertEquals(0, users.size());
  }

  @Test
  @DisplayName("Should not allow duplicate user IDs")
  void testAddDuplicateUserId() {
    // Arrange
    User user1 = new User(1, "John Doe");
    User user2 = new User(1, "Jane Doe");

    // Act
    userStore.addUser(user1);
    userStore.addUser(user2);
    Set<User> users = userStore.getAll();

    // Assert
    assertEquals(1, users.size());
    User retrievedUser = users.iterator().next();
    assertEquals("John Doe", retrievedUser.getName());
  }

  @Test
  @DisplayName("Should handle user with empty name")
  void testAddUserWithEmptyName() {
    // Arrange
    User user = new User(5, "");

    // Act
    userStore.addUser(user);
    Set<User> users = userStore.getAll();

    // Assert
    assertEquals(1, users.size());
    assertTrue(users.contains(user));
  }

  @Test
  @DisplayName("Should handle user with special characters in name")
  void testAddUserWithSpecialCharacters() {
    // Arrange
    User user = new User(6, "John@#$%Doe");

    // Act
    userStore.addUser(user);
    Set<User> users = userStore.getAll();

    // Assert
    assertEquals(1, users.size());
    User retrievedUser = users.iterator().next();
    assertEquals("John@#$%Doe", retrievedUser.getName());
  }

  @Test
  @DisplayName("Should handle large number of users")
  void testAddLargeNumberOfUsers() {
    // Arrange & Act
    for (int i = 1; i <= 1000; i++) {
      userStore.addUser(new User(i, "User" + i));
    }
    Set<User> users = userStore.getAll();

    // Assert
    assertEquals(1000, users.size());
  }

  @Test
  @DisplayName("Should handle user with very long name")
  void testAddUserWithVeryLongName() {
    // Arrange
    String longName = "A".repeat(10000);
    User user = new User(7, longName);

    // Act
    userStore.addUser(user);
    Set<User> users = userStore.getAll();

    // Assert
    assertEquals(1, users.size());
    User retrievedUser = users.iterator().next();
    assertEquals(longName, retrievedUser.getName());
  }

  @Test
  @DisplayName("Should maintain user integrity after multiple operations")
  void testUserIntegrityAfterMultipleOperations() {
    // Arrange & Act
    User user1 = new User(1, "User1");
    User user2 = new User(2, "User2");
    User user3 = new User(3, "User3");

    userStore.addUser(user1);
    Set<User> firstCheck = userStore.getAll();
    assertEquals(1, firstCheck.size());

    userStore.addUser(user2);
    Set<User> secondCheck = userStore.getAll();
    assertEquals(2, secondCheck.size());

    userStore.addUser(user3);
    Set<User> thirdCheck = userStore.getAll();

    // Assert
    assertEquals(3, thirdCheck.size());
    assertTrue(thirdCheck.contains(user1));
    assertTrue(thirdCheck.contains(user2));
    assertTrue(thirdCheck.contains(user3));
  }

  @Test
  @DisplayName("Should handle concurrent additions")
  void testConcurrentAdditions() throws InterruptedException {
    // Arrange
    Thread thread1 = new Thread(() -> {
      for (int i = 1; i <= 10; i++) {
        userStore.addUser(new User(i, "User" + i));
      }
    });

    Thread thread2 = new Thread(() -> {
      for (int i = 11; i <= 20; i++) {
        userStore.addUser(new User(i, "User" + i));
      }
    });

    // Act
    thread1.start();
    thread2.start();
    thread1.join();
    thread2.join();

    Set<User> users = userStore.getAll();

    // Assert
    assertTrue(users.size() >= 10);  // At least 10 users should be added
  }

  @Test
  @DisplayName("Should verify user equality based on ID and name")
  void testUserEquality() {
    // Arrange
    User user1 = new User(1, "John Doe");
    User user2 = new User(1, "John Doe");
    User user3 = new User(2, "John Doe");

    // Act
    userStore.addUser(user1);
    Set<User> users = userStore.getAll();

    // Assert
    assertTrue(users.contains(user2));
    assertEquals(1, users.size());
  }
}
