package com.inventory.system.auth.service;

import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.inventory.system.activity.enums.ActionType;
import com.inventory.system.activity.enums.ModuleType;
import com.inventory.system.activity.service.ActivityLogService;
import com.inventory.system.auth.dto.LoginRequest;
import com.inventory.system.auth.dto.LoginResponse;
import com.inventory.system.exception.BadRequestException;
import com.inventory.system.security.JwtService;
import com.inventory.system.user.entity.User;
import com.inventory.system.user.repository.UserRepository;

@Service
@Transactional
public class AuthService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;

    private final ActivityLogService activityLogService;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            ActivityLogService activityLogService
    ) {

        this.userRepository = userRepository;

        this.passwordEncoder = passwordEncoder;

        this.jwtService = jwtService;

        this.activityLogService = activityLogService;
    }

    // =====================================================
    // LOGIN
    // =====================================================

    public LoginResponse login(
            LoginRequest request
    ) {

        // =====================================================
        // SESSION ID
        // =====================================================

        String sessionId =
                UUID.randomUUID().toString();

        // =====================================================
        // FIND USER
        // =====================================================

        User user = userRepository.findByUsername(
                request.getUsername()
        ).orElse(null);

        // =====================================================
        // USER NOT FOUND
        // =====================================================

        if (user == null) {

            activityLogService.log(

                    null,

                    request.getUsername(),

                    "UNKNOWN",

                    ModuleType.AUTH,

                    ActionType.LOGIN,

                    "Failed login attempt: invalid username",

                    null,

                    request.getUsername(),

                    sessionId,

                    "FAILED"
            );

            throw new BadRequestException(
                    "Invalid username or password"
            );
        }

        // =====================================================
        // ACTIVE CHECK
        // =====================================================

        if (Boolean.FALSE.equals(
                user.getActive()
        )) {

            activityLogService.log(

                    user.getId(),

                    user.getUsername(),

                    user.getRole().getName(),

                    ModuleType.AUTH,

                    ActionType.LOGIN,

                    "Failed login attempt: inactive account",

                    user.getId(),

                    user.getName(),

                    sessionId,

                    "FAILED"
            );

            throw new BadRequestException(
                    "User account is inactive"
            );
        }

        // =====================================================
        // PASSWORD VALIDATION
        // =====================================================

        boolean passwordMatch =
                passwordEncoder.matches(
                        request.getPassword(),
                        user.getPassword()
                );

        // =====================================================
        // WRONG PASSWORD
        // =====================================================

        if (!passwordMatch) {

            activityLogService.log(

                    user.getId(),

                    user.getUsername(),

                    user.getRole().getName(),

                    ModuleType.AUTH,

                    ActionType.LOGIN,

                    "Failed login attempt: wrong password",

                    user.getId(),

                    user.getName(),

                    sessionId,

                    "FAILED"
            );

            throw new BadRequestException(
                    "Invalid username or password"
            );
        }

        // =====================================================
        // GENERATE TOKEN
        // =====================================================

        String token =
                jwtService.generateToken(
                        user.getUsername()
                );

        // =====================================================
        // SUCCESS LOGIN LOG
        // =====================================================

        activityLogService.log(

                user.getId(),

                user.getUsername(),

                user.getRole().getName(),

                ModuleType.AUTH,

                ActionType.LOGIN,

                user.getUsername()
                        + " logged into the system",

                user.getId(),

                user.getName(),

                sessionId,

                "SUCCESS"
        );

        // =====================================================
        // RESPONSE
        // =====================================================

        return new LoginResponse(

                token,
                user.getName(),
                user.getId(),
                user.getEmail(),
                user.getRole().getName(),
                sessionId
        );
    }
}