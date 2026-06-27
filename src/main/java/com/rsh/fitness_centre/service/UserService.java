package com.rsh.fitness_centre.service;

import com.rsh.fitness_centre.entity.User;
import com.rsh.fitness_centre.entity.UserRole;
import com.rsh.fitness_centre.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

  private static final Logger logger = LoggerFactory.getLogger(UserService.class);
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  @Autowired
  public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
  }

  public User addUser(String name) {
    logger.info("Adding user: {}", name);
    User user = new User(null, name);
    User savedUser = userRepository.save(user);
    logger.info("User created successfully with ID: {}", savedUser.getId());
    return savedUser;
  }

  public Set<User> getAllUsers() {
    logger.debug("Retrieving all users");
    Set<User> users = new HashSet<>(userRepository.findAll());
    logger.debug("Found {} users", users.size());
    return users;
  }

  public User getUserById(Long id) {
    logger.debug("Retrieving user by ID: {}", id);
    User user = userRepository.findById(id).orElse(null);
    if (user == null) {
      logger.warn("User not found with ID: {}", id);
    }
    return user;
  }

  public User getUserByName(String name) {
    logger.debug("Retrieving user by name: {}", name);
    return userRepository.findByName(name).orElse(null);
  }

  /**
   * Register a new user with email and password.
   *
   * @param email the user email
   * @param password the user password (will be hashed)
   * @param name the user name
   * @return the created user with USER role
   * @throws IllegalArgumentException if user with email already exists
   */
  public User registerUser(String email, String password, String name) {
    logger.info("Registering new user with email: {}", email);

    if (userRepository.findByEmail(email).isPresent()) {
      logger.warn("User registration failed: email already exists: {}", email);
      throw new IllegalArgumentException("User with email already exists: " + email);
    }

    User user = new User();
    user.setEmail(email);
    user.setName(name);
    user.setPasswordHash(passwordEncoder.encode(password));
    user.setEnabled(true);
    user.setRoles(Set.of(UserRole.USER));

    User savedUser = userRepository.save(user);
    logger.info("User registered successfully with ID: {} and email: {}", savedUser.getId(), email);
    return savedUser;
  }

  /**
   * Authenticate user with email and password.
   *
   * @param email the user email
   * @param password the user password
   * @return the authenticated user with roles
   * @throws IllegalArgumentException if credentials are invalid
   */
  public User authenticateUser(String email, String password) {
    logger.debug("Authenticating user with email: {}", email);

    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> {
          logger.warn("Authentication failed: user not found with email: {}", email);
          return new IllegalArgumentException("Invalid email or password");
        });

    if (!user.isEnabled()) {
      logger.warn("Authentication failed: user account is disabled: {}", email);
      throw new IllegalArgumentException("User account is disabled");
    }

    if (!passwordEncoder.matches(password, user.getPasswordHash())) {
      logger.warn("Authentication failed: invalid password for user: {}", email);
      throw new IllegalArgumentException("Invalid email or password");
    }

    user.setLastLogin(LocalDateTime.now());
    User updatedUser = userRepository.save(user);
    logger.info("User authenticated successfully: {}", email);
    return updatedUser;
  }

  /**
   * Get user with roles eagerly loaded.
   *
   * @param userId the user ID
   * @return the user with roles
   */
  public User getUserWithRoles(Long userId) {
    logger.debug("Retrieving user with roles by ID: {}", userId);
    return userRepository.findWithRolesById(userId)
        .orElse(null);
  }

  /**
   * Get user by email.
   *
   * @param email the user email
   * @return the user or null if not found
   */
  public User getUserByEmail(String email) {
    logger.debug("Retrieving user by email: {}", email);
    return userRepository.findByEmail(email).orElse(null);
  }
}
