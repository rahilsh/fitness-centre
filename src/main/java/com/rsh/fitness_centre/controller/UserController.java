package com.rsh.fitness_centre.controller;

import com.rsh.fitness_centre.entity.User;
import com.rsh.fitness_centre.entity.request.AddUserRequest;
import com.rsh.fitness_centre.service.UserService;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {

  private final UserService userService;

  @Autowired
  public UserController(UserService userService) {
    this.userService = userService;
  }

  @PutMapping
  public User addUser(@RequestBody AddUserRequest addUserRequest) {
    return userService.addUser(addUserRequest.getName());
  }

  @GetMapping
  public Set<User> addUser() {
    return userService.getAllUsers();
  }
}
