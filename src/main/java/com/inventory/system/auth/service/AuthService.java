package com.inventory.system.auth.service;

import com.inventory.system.exception.BadRequestException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.inventory.system.auth.dto.LoginRequest;
import com.inventory.system.auth.dto.LoginResponse;
import com.inventory.system.security.JwtService;
import com.inventory.system.user.entity.User;
import com.inventory.system.user.repository.UserRepository;

@Service
@Transactional(readOnly = true)
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public LoginResponse login(LoginRequest request) {

        User user = userRepository.findByUsername(
                request.getUsername()
        ).orElseThrow(() ->
                new BadRequestException(
                        "Invalid username or password"
                )
        );

        // Active check
        if (Boolean.FALSE.equals(user.getActive())) {

            throw new BadRequestException(
                    "User account is inactive"
            );
        }

        // Password validation
        boolean passwordMatch =
                passwordEncoder.matches(
                        request.getPassword(),
                        user.getPassword()
                );

        if (!passwordMatch) {

            throw new BadRequestException(
                    "Invalid username or password"
            );
        }

        String token =
                jwtService.generateToken(
                        user.getUsername()
                );

        return new LoginResponse(
                token,
                user.getName(),
                user.getId(),
                user.getEmail(),
                user.getRole().getName()
        );
    }
}