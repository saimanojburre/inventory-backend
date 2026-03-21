package com.inventory.system.user.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.inventory.system.user.dto.CreateUserRequest;
import com.inventory.system.user.dto.UpdateUserRequest;
import com.inventory.system.user.entity.User;
import com.inventory.system.user.service.UserService;

@RestController
@RequestMapping("/users")
public class UserController {

	private final UserService userService;

	public UserController(UserService userService) {
		this.userService = userService;
	}

	@PostMapping
	public User createUser(@RequestBody CreateUserRequest request) {
		return userService.createUser(request);
	}

	@GetMapping
	public List<User> getAllUsers() {
		return userService.getAllUsers();
	}

	@GetMapping("/{id}")
	public User getUser(@PathVariable Long id) {
		return userService.getUserById(id);
	}

	@PutMapping("/{id}")
	public User updateUser(@PathVariable Long id, @RequestBody UpdateUserRequest request) {

		return userService.updateUser(id, request);
	}

	@DeleteMapping("/{id}")
	public String deleteUser(@PathVariable Long id) {

		userService.deleteUser(id);

		return "User deleted successfully";
	}
}