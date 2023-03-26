package com.rsh.fitness_centre.service;

import com.rsh.fitness_centre.entity.User;
import com.rsh.fitness_centre.store.UserStore;
import com.rsh.fitness_centre.util.SequenceGenerator;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

  private final SequenceGenerator sequenceGenerator;
  private final UserStore userStore;

  @Autowired
  public UserService(SequenceGenerator sequenceGenerator, UserStore userStore) {
    this.sequenceGenerator = sequenceGenerator;
    this.userStore = userStore;
  }

  public User addUser(String name) {
    User user = new User(sequenceGenerator.getNext("User"), name);
    userStore.addUser(user);
    return user;
  }

  public Set<User> getAllUsers() {
    return userStore.getAll();
  }
}
