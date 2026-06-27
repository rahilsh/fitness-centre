package com.rsh.fitness_centre.controller;

import com.rsh.fitness_centre.entity.User;
import com.rsh.fitness_centre.entity.request.AddUserRequest;
import com.rsh.fitness_centre.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@Tag(name = "Users", description = "User management endpoints")
public class UserController {

  private final UserService userService;

  @Autowired
  public UserController(UserService userService) {
    this.userService = userService;
  }

  @PostMapping
  @Operation(summary = "Create a new user", description = "Register a new user with their name")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "User created successfully",
          content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))),
      @ApiResponse(responseCode = "400", description = "Invalid input - name is required and must be 2-100 characters")
  })
  public ResponseEntity<User> addUser(@Valid @RequestBody AddUserRequest addUserRequest) {
    User user = userService.addUser(addUserRequest.getName());
    return ResponseEntity.status(HttpStatus.CREATED).body(user);
  }

  @GetMapping
  @Operation(summary = "Retrieve all users", description = "Get a list of all registered users")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Users retrieved successfully",
          content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class)))
  })
  public Set<User> getAllUsers() {
    return userService.getAllUsers();
  }
}
