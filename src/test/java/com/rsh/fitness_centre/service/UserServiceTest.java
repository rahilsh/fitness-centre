package com.rsh.fitness_centre.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.rsh.fitness_centre.entity.User;
import com.rsh.fitness_centre.store.UserStore;
import com.rsh.fitness_centre.util.SequenceGenerator;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService Tests")
class UserServiceTest {

  private UserService userService;

  @Mock
  private SequenceGenerator sequenceGenerator;

  @Mock
  private UserStore userStore;

  @BeforeEach
  void setUp() {
    userService = new UserService(sequenceGenerator, userStore);
  }

  @Test
  @DisplayName("Should add user successfully with valid name")
  void testAddUserSuccess() {
    // Arrange
    String userName = "John Doe";
    int userId = 1;
    when(sequenceGenerator.getNext("User")).thenReturn(userId);

    // Act
    User result = userService.addUser(userName);

    // Assert
    assertNotNull(result);
    assertEquals(userId, result.getId());
    assertEquals(userName, result.getName());
    verify(sequenceGenerator, times(1)).getNext("User");
    verify(userStore, times(1)).addUser(result);
  }

  @Test
  @DisplayName("Should add user successfully with empty name")
  void testAddUserWithEmptyName() {
    // Arrange
    String userName = "";
    int userId = 2;
    when(sequenceGenerator.getNext("User")).thenReturn(userId);

    // Act
    User result = userService.addUser(userName);

    // Assert
    assertNotNull(result);
    assertEquals(userId, result.getId());
    assertEquals(userName, result.getName());
    verify(userStore, times(1)).addUser(result);
  }

  @Test
  @DisplayName("Should get all users successfully")
  void testGetAllUsersSuccess() {
    // Arrange
    Set<User> users = new HashSet<>();
    users.add(new User(1, "John Doe"));
    users.add(new User(2, "Jane Doe"));
    when(userStore.getAll()).thenReturn(users);

    // Act
    Set<User> result = userService.getAllUsers();

    // Assert
    assertNotNull(result);
    assertEquals(2, result.size());
    verify(userStore, times(1)).getAll();
  }

  @Test
  @DisplayName("Should get empty set when no users exist")
  void testGetAllUsersEmpty() {
    // Arrange
    Set<User> emptySet = new HashSet<>();
    when(userStore.getAll()).thenReturn(emptySet);

    // Act
    Set<User> result = userService.getAllUsers();

    // Assert
    assertNotNull(result);
    assertEquals(0, result.size());
    verify(userStore, times(1)).getAll();
  }

  @Test
  @DisplayName("Should generate sequential user IDs")
  void testSequentialUserIds() {
    // Arrange
    when(sequenceGenerator.getNext("User")).thenReturn(1).thenReturn(2).thenReturn(3);

    // Act
    User user1 = userService.addUser("User1");
    User user2 = userService.addUser("User2");
    User user3 = userService.addUser("User3");

    // Assert
    assertEquals(1, user1.getId());
    assertEquals(2, user2.getId());
    assertEquals(3, user3.getId());
    verify(sequenceGenerator, times(3)).getNext("User");
  }

  @Test
  @DisplayName("Should handle special characters in user name")
  void testAddUserWithSpecialCharacters() {
    // Arrange
    String userName = "John@#$%Doe";
    int userId = 5;
    when(sequenceGenerator.getNext("User")).thenReturn(userId);

    // Act
    User result = userService.addUser(userName);

    // Assert
    assertNotNull(result);
    assertEquals(userName, result.getName());
  }

  @Test
  @DisplayName("Should handle very long user name")
  void testAddUserWithLongName() {
    // Arrange
    String userName = "A".repeat(1000);
    int userId = 6;
    when(sequenceGenerator.getNext("User")).thenReturn(userId);

    // Act
    User result = userService.addUser(userName);

    // Assert
    assertNotNull(result);
    assertEquals(userName, result.getName());
  }
}
