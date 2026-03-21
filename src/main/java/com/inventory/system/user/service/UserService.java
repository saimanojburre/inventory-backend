package com.inventory.system.user.service;

import org.springframework.stereotype.Service;

import com.inventory.system.exception.ResourceNotFoundException;
import com.inventory.system.role.entity.Role;
import com.inventory.system.role.repository.RoleRepository;
import com.inventory.system.user.dto.CreateUserRequest;
import com.inventory.system.user.dto.UpdateUserRequest;
import com.inventory.system.user.entity.User;
import com.inventory.system.user.repository.UserRepository;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;

@Service
public class UserService {

	private final UserRepository userRepository;
	private final RoleRepository roleRepository;
	private final PasswordEncoder passwordEncoder;

	public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.roleRepository = roleRepository;
		this.passwordEncoder = passwordEncoder;
	}

	public User createUser(CreateUserRequest request) {

		Role role = roleRepository.findByName(request.getRole())
				.orElseThrow(() -> new RuntimeException("Role not found"));

		User user = new User();
		user.setName(request.getName());
		user.setUsername(request.getUsername());
		user.setPhone(request.getPhone());
		user.setEmail(request.getEmail());
		user.setPassword(passwordEncoder.encode(request.getPassword()));
		user.setRole(role);

		return userRepository.save(user);
	}

	public List<User> getAllUsers() {
		return userRepository.findAll();
	}

	public User getUserById(Long id) {

		return userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found"));
	}

	public User updateUser(Long id, UpdateUserRequest request) {

		User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found"));

		// Update basic fields
		if (request.getName() != null) {
			user.setName(request.getName());
		}

		if (request.getEmail() != null) {
			user.setEmail(request.getEmail());
		}

		if (request.getPhone() != null) {
			user.setPhone(request.getPhone());
		}

		// Update password only if provided
		if (request.getPassword() != null && !request.getPassword().isBlank()) {
			user.setPassword(passwordEncoder.encode(request.getPassword()));
		}

		// Update role only if provided
		if (request.getRole() != null) {

			Role role = roleRepository.findByName(request.getRole())
					.orElseThrow(() -> new RuntimeException("Role not found"));

			user.setRole(role);
		}

		return userRepository.save(user);
	}

	public void deleteUser(Long id) {

		User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found"));

		userRepository.delete(user);
	}
}