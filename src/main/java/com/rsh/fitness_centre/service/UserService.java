package com.rsh.fitness_centre.service;

import com.rsh.fitness_centre.entity.User;
import com.rsh.fitness_centre.repository.UserRepository;
import java.util.HashSet;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

  private static final Logger logger = LoggerFactory.getLogger(UserService.class);
  private final UserRepository userRepository;

  @Autowired
  public UserService(UserRepository userRepository) {
    this.userRepository = userRepository;
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
}
