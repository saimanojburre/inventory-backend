package com.inventory.system.auth.controller;

import org.springframework.web.bind.annotation.*;

import com.inventory.system.auth.dto.LoginRequest;
import com.inventory.system.auth.dto.LoginResponse;
import com.inventory.system.auth.service.AuthService;

@RestController
@RequestMapping("/auth")
public class AuthController {

	private final AuthService authService;

	public AuthController(AuthService authService) {
		this.authService = authService;
	}

	@PostMapping("/login")
	public LoginResponse login(@RequestBody LoginRequest request) {

	    return authService.login(request);

	}
}