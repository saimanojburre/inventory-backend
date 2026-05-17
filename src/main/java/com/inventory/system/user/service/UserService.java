package com.inventory.system.user.service;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.inventory.system.activity.enums.ActionType;
import com.inventory.system.activity.enums.ModuleType;
import com.inventory.system.activity.service.ActivityLogService;
import com.inventory.system.exception.BadRequestException;
import com.inventory.system.exception.ResourceNotFoundException;
import com.inventory.system.role.entity.Role;
import com.inventory.system.role.repository.RoleRepository;
import com.inventory.system.user.dto.CreateUserRequest;
import com.inventory.system.user.dto.UpdateUserRequest;
import com.inventory.system.user.entity.User;
import com.inventory.system.user.repository.UserRepository;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final PasswordEncoder passwordEncoder;

    private final ActivityLogService activityLogService;

    public UserService(
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder,
            ActivityLogService activityLogService
    ) {

        this.userRepository = userRepository;

        this.roleRepository = roleRepository;

        this.passwordEncoder = passwordEncoder;

        this.activityLogService = activityLogService;
    }

    // =====================================================
    // CREATE USER
    // =====================================================

    public User createUser(
            CreateUserRequest request,
            String sessionId
    ) {

        // =====================================================
        // EMAIL VALIDATION
        // =====================================================

        if (userRepository.existsByEmail(
                request.getEmail()
        )) {

            throw new BadRequestException(
                    "Email already exists"
            );
        }

        // =====================================================
        // USERNAME VALIDATION
        // =====================================================

        if (userRepository.existsByUsername(
                request.getUsername()
        )) {

            throw new BadRequestException(
                    "Username already exists"
            );
        }

        User user = new User();

        user.setName(request.getName());

        user.setUsername(request.getUsername());

        user.setPhone(request.getPhone());

        user.setEmail(request.getEmail());

        // =====================================================
        // ENCRYPT PASSWORD
        // =====================================================

        user.setPassword(
                passwordEncoder.encode(
                        request.getPassword()
                )
        );

        // =====================================================
        // ADMIN FLOW
        // =====================================================

        if (request.getRole() != null) {

            Role role = roleRepository.findByName(
                    request.getRole()
            ).orElseThrow(() ->

                    new ResourceNotFoundException(
                            "Role not found"
                    )
            );

            user.setRole(role);

            user.setActive(

                    request.getActive() != null
                            ? request.getActive()
                            : true
            );
        }

        // =====================================================
        // PUBLIC REGISTRATION FLOW
        // =====================================================

        else {

            Role defaultRole =
                    roleRepository.findByName(
                            "USER"
                    ).orElseThrow(() ->

                            new ResourceNotFoundException(
                                    "Role not found"
                            )
                    );

            user.setRole(defaultRole);

            user.setActive(false);
        }

        User savedUser =
                userRepository.save(user);

        User currentUser =
                getCurrentUser();

        // =====================================================
        // ACTIVITY LOG
        // =====================================================

        if (currentUser != null) {

            activityLogService.log(

                    currentUser.getId(),

                    currentUser.getUsername(),

                    currentUser.getRole().getName(),

                    ModuleType.USER,

                    ActionType.CREATE,

                    buildCreateDescription(savedUser),

                    savedUser.getId(),

                    savedUser.getUsername(),

                    sessionId,

                    "SUCCESS"
            );
        }

        return savedUser;
    }

    // =====================================================
    // GET ALL USERS
    // =====================================================

    @Transactional(readOnly = true)
    public List<User> getAllUsers() {

        return userRepository.findAll();
    }

    // =====================================================
    // GET USER BY ID
    // =====================================================

    @Transactional(readOnly = true)
    public User getUserById(
            Long id
    ) {

        return userRepository.findById(id)
                .orElseThrow(() ->

                        new ResourceNotFoundException(
                                "User not found"
                        )
                );
    }

    // =====================================================
    // UPDATE USER
    // =====================================================

    public User updateUser(

            Long id,

            UpdateUserRequest request,

            String sessionId
    ) {

        User user =
                userRepository.findById(id)
                        .orElseThrow(() ->

                                new ResourceNotFoundException(
                                        "User not found"
                                )
                        );

        // =====================================================
        // UPDATE NAME
        // =====================================================

        if (request.getName() != null) {

            user.setName(
                    request.getName()
            );
        }

        // =====================================================
        // UPDATE EMAIL
        // =====================================================

        if (request.getEmail() != null &&
                !request.getEmail().equals(
                        user.getEmail()
                )) {

            if (userRepository.existsByEmail(
                    request.getEmail()
            )) {

                throw new BadRequestException(
                        "Email already exists"
                );
            }

            user.setEmail(
                    request.getEmail()
            );
        }

        // =====================================================
        // UPDATE PHONE
        // =====================================================

        if (request.getPhone() != null) {

            user.setPhone(
                    request.getPhone()
            );
        }

        // =====================================================
        // UPDATE PASSWORD
        // =====================================================

        if (request.getPassword() != null &&
                !request.getPassword().isBlank()) {

            user.setPassword(

                    passwordEncoder.encode(
                            request.getPassword()
                    )
            );
        }

        // =====================================================
        // UPDATE ROLE
        // =====================================================

        if (request.getRole() != null) {

            Role role =
                    roleRepository.findByName(
                            request.getRole()
                    ).orElseThrow(() ->

                            new ResourceNotFoundException(
                                    "Role not found"
                            )
                    );

            user.setRole(role);
        }

        // =====================================================
        // UPDATE ACTIVE STATUS
        // =====================================================

        if (request.getActive() != null) {

            user.setActive(
                    request.getActive()
            );
        }

        User updatedUser =
                userRepository.save(user);

        User currentUser =
                getCurrentUser();

        // =====================================================
        // ACTIVITY LOG
        // =====================================================

        activityLogService.log(

                currentUser.getId(),

                currentUser.getUsername(),

                currentUser.getRole().getName(),

                ModuleType.USER,

                ActionType.UPDATE,

                buildUpdateDescription(updatedUser),

                updatedUser.getId(),

                updatedUser.getUsername(),

                sessionId,

                "SUCCESS"
        );

        return updatedUser;
    }

    // =====================================================
    // DELETE USER
    // =====================================================

    public void deleteUser(
            Long id,
            String sessionId
    ) {

        User user =
                userRepository.findById(id)
                        .orElseThrow(() ->

                                new ResourceNotFoundException(
                                        "User not found"
                                )
                        );

        userRepository.deleteById(id);

        User currentUser =
                getCurrentUser();

        // =====================================================
        // ACTIVITY LOG
        // =====================================================

        activityLogService.log(

                currentUser.getId(),

                currentUser.getUsername(),

                currentUser.getRole().getName(),

                ModuleType.USER,

                ActionType.DELETE,

                buildDeleteDescription(user),

                user.getId(),

                user.getUsername(),

                sessionId,

                "SUCCESS"
        );
    }

    // =====================================================
    // CURRENT USER
    // =====================================================

    private User getCurrentUser() {

        try {

            Authentication authentication =
                    SecurityContextHolder
                            .getContext()
                            .getAuthentication();

            if (authentication == null) {

                return null;
            }

            return (User) authentication.getPrincipal();

        } catch (Exception ex) {

            return null;
        }
    }

    // =====================================================
    // DESCRIPTION HELPERS
    // =====================================================

    private String buildCreateDescription(
            User user
    ) {

        return "Created user "
                + user.getUsername()
                + " with role "
                + user.getRole().getName();
    }

    private String buildUpdateDescription(
            User user
    ) {

        return "Updated user "
                + user.getUsername()
                + " with role "
                + user.getRole().getName();
    }

    private String buildDeleteDescription(
            User user
    ) {

        return "Deleted user "
                + user.getUsername();
    }
}