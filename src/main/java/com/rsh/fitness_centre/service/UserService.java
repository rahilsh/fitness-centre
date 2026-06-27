package com.rsh.fitness_centre.service;

import com.rsh.fitness_centre.entity.User;
import com.rsh.fitness_centre.repository.UserRepository;
import com.rsh.fitness_centre.util.SequenceGenerator;
import java.util.HashSet;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

  private final SequenceGenerator sequenceGenerator;
  private final UserRepository userRepository;

  @Autowired
  public UserService(SequenceGenerator sequenceGenerator, UserRepository userRepository) {
    this.sequenceGenerator = sequenceGenerator;
    this.userRepository = userRepository;
  }

  public User addUser(String name) {
    User user = new User(sequenceGenerator.getNext("User"), name);
    userRepository.save(user);
    return user;
  }

  public Set<User> getAllUsers() {
    return new HashSet<>(userRepository.findAll());
  }
}
