package com.rsh.fitness_centre.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.rsh.fitness_centre.entity.User;
import com.rsh.fitness_centre.repository.UserRepository;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService Tests")
class UserServiceTest {

  private UserService userService;

  @Mock
  private UserRepository userRepository;

  @Mock
  private PasswordEncoder passwordEncoder;

  @BeforeEach
  void setUp() {
    userService = new UserService(userRepository, passwordEncoder);
  }

  @Test
  @DisplayName("Should add user successfully with valid name")
  void testAddUserSuccess() {
    // Arrange
    String userName = "John Doe";
    User expectedUser = new User(1L, userName);
    when(userRepository.save(any(User.class))).thenReturn(expectedUser);

    // Act
    User result = userService.addUser(userName);

    // Assert
    assertNotNull(result);
    assertEquals(1L, result.getId());
    assertEquals(userName, result.getName());
    verify(userRepository, times(1)).save(any(User.class));
  }

  @Test
  @DisplayName("Should add user successfully with empty name")
  void testAddUserWithEmptyName() {
    // Arrange
    String userName = "";
    User expectedUser = new User(2L, userName);
    when(userRepository.save(any(User.class))).thenReturn(expectedUser);

    // Act
    User result = userService.addUser(userName);

    // Assert
    assertNotNull(result);
    assertEquals(2L, result.getId());
    assertEquals(userName, result.getName());
    verify(userRepository, times(1)).save(any(User.class));
  }

  @Test
  @DisplayName("Should get all users successfully")
  void testGetAllUsersSuccess() {
    // Arrange
    Set<User> users = new HashSet<>();
    users.add(new User(1L, "John Doe"));
    users.add(new User(2L, "Jane Doe"));
    when(userRepository.findAll()).thenReturn(new ArrayList<>(users));

    // Act
    Set<User> result = userService.getAllUsers();

    // Assert
    assertNotNull(result);
    assertEquals(2, result.size());
    verify(userRepository, times(1)).findAll();
  }

  @Test
  @DisplayName("Should get empty set when no users exist")
  void testGetAllUsersEmpty() {
    // Arrange
    List<User> emptyList = new ArrayList<>();
    when(userRepository.findAll()).thenReturn(emptyList);

    // Act
    Set<User> result = userService.getAllUsers();

    // Assert
    assertNotNull(result);
    assertEquals(0, result.size());
    verify(userRepository, times(1)).findAll();
  }

  @Test
  @DisplayName("Should get user by ID successfully")
  void testGetUserByIdSuccess() {
    // Arrange
    Long userId = 1L;
    User mockUser = new User(userId, "John Doe");
    when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));

    // Act
    User result = userService.getUserById(userId);

    // Assert
    assertNotNull(result);
    assertEquals(userId, result.getId());
    assertEquals("John Doe", result.getName());
    verify(userRepository, times(1)).findById(userId);
  }

  @Test
  @DisplayName("Should return null when user ID does not exist")
  void testGetUserByIdNotFound() {
    // Arrange
    Long userId = 999L;
    when(userRepository.findById(userId)).thenReturn(Optional.empty());

    // Act
    User result = userService.getUserById(userId);

    // Assert
    assertNull(result);
    verify(userRepository, times(1)).findById(userId);
  }

  @Test
  @DisplayName("Should get user by name successfully")
  void testGetUserByNameSuccess() {
    // Arrange
    String userName = "John Doe";
    User mockUser = new User(1L, userName);
    when(userRepository.findByName(userName)).thenReturn(Optional.of(mockUser));

    // Act
    User result = userService.getUserByName(userName);

    // Assert
    assertNotNull(result);
    assertEquals(1L, result.getId());
    assertEquals(userName, result.getName());
    verify(userRepository, times(1)).findByName(userName);
  }

  @Test
  @DisplayName("Should return null when user name does not exist")
  void testGetUserByNameNotFound() {
    // Arrange
    String userName = "Non-existent User";
    when(userRepository.findByName(userName)).thenReturn(Optional.empty());

    // Act
    User result = userService.getUserByName(userName);

    // Assert
    assertNull(result);
    verify(userRepository, times(1)).findByName(userName);
  }

  @Test
  @DisplayName("Should handle special characters in user name")
  void testAddUserWithSpecialCharacters() {
    // Arrange
    String userName = "John@#$%Doe";
    User expectedUser = new User(5L, userName);
    when(userRepository.save(any(User.class))).thenReturn(expectedUser);

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
    User expectedUser = new User(6L, userName);
    when(userRepository.save(any(User.class))).thenReturn(expectedUser);

    // Act
    User result = userService.addUser(userName);

    // Assert
    assertNotNull(result);
    assertEquals(userName, result.getName());
  }
}
