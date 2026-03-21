package com.inventory.system.auth.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.inventory.system.auth.dto.LoginRequest;
import com.inventory.system.auth.dto.LoginResponse;
import com.inventory.system.security.JwtService;
import com.inventory.system.user.entity.User;
import com.inventory.system.user.repository.UserRepository;

@Service
public class AuthService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtService jwtService;

	public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.jwtService = jwtService;
	}

	public LoginResponse login(LoginRequest request) {

		User user = userRepository.findByUsername(request.getUsername())
				.orElseThrow(() -> new RuntimeException("User not found"));

		boolean passwordMatch = passwordEncoder.matches(request.getPassword(), user.getPassword());

		if (!passwordMatch) {
			throw new RuntimeException("Invalid password");
		}

		String token = jwtService.generateToken(user.getUsername());

		return new LoginResponse(token, user.getName(), user.getId(), user.getEmail(), user.getRole().getName());
	}
}