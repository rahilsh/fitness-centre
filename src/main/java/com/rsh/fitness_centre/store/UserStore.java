package com.rsh.fitness_centre.store;

import com.rsh.fitness_centre.entity.User;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.springframework.stereotype.Service;

@Service
public class UserStore {

  private final Map<Integer, User> users = new HashMap<>();

  public void addUser(User user) {

    users.putIfAbsent(user.getId(), user);
  }

  public Set<User> getAll() {
    Set<User> usersSet = new HashSet<>();
    users.forEach((k, v) -> usersSet.add(v));
    return usersSet;
  }
}
