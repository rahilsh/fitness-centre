package com.rsh.fitness_centre.service;

import com.rsh.fitness_centre.entity.User;
import com.rsh.fitness_centre.repository.UserRepository;
import java.util.HashSet;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

  private final UserRepository userRepository;

  @Autowired
  public UserService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public User addUser(String name) {
    User user = new User(null, name);
    return userRepository.save(user);
  }

  public Set<User> getAllUsers() {
    return new HashSet<>(userRepository.findAll());
  }

  public User getUserById(Long id) {
    return userRepository.findById(id).orElse(null);
  }

  public User getUserByName(String name) {
    return userRepository.findByName(name).orElse(null);
  }
}
