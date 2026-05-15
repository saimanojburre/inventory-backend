package com.inventory.system.user.service;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public UserService(
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User createUser(CreateUserRequest request) {

        // Email validation
        if (userRepository.existsByEmail(request.getEmail())) {

            throw new BadRequestException(
                    "Email already exists"
            );
        }

        // Username validation
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

        // Encrypt password
        user.setPassword(
                passwordEncoder.encode(
                        request.getPassword()
                )
        );

        // ADMIN FLOW
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

        // PUBLIC REGISTRATION FLOW
        else {

            Role defaultRole = roleRepository.findByName(
                    "USER"
            ).orElseThrow(() ->
                    new ResourceNotFoundException(
                            "Role not found"
                    )
            );

            user.setRole(defaultRole);

            user.setActive(false);
        }

        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public List<User> getAllUsers() {

        return userRepository.findAll();
    }

    @Transactional(readOnly = true)
    public User getUserById(Long id) {

        return userRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found"
                        )
                );
    }

    public User updateUser(
            Long id,
            UpdateUserRequest request
    ) {

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found"
                        )
                );

        // Update name
        if (request.getName() != null) {
            user.setName(request.getName());
        }

        // Update email
        if (request.getEmail() != null &&
                !request.getEmail().equals(user.getEmail())) {

            if (userRepository.existsByEmail(
                    request.getEmail()
            )) {

                throw new BadRequestException(
                        "Email already exists"
                );
            }

            user.setEmail(request.getEmail());
        }

        // Update phone
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }

        // Update password only if provided
        if (request.getPassword() != null &&
                !request.getPassword().isBlank()) {

            user.setPassword(
                    passwordEncoder.encode(
                            request.getPassword()
                    )
            );
        }

        // Update role
        if (request.getRole() != null) {

            Role role = roleRepository.findByName(
                    request.getRole()
            ).orElseThrow(() ->
                    new ResourceNotFoundException(
                            "Role not found"
                    )
            );

            user.setRole(role);
        }

        // Update active status
        if (request.getActive() != null) {
            user.setActive(request.getActive());
        }

        return userRepository.save(user);
    }

    public void deleteUser(Long id) {

        if (!userRepository.existsById(id)) {

            throw new ResourceNotFoundException(
                    "User not found"
            );
        }

        userRepository.deleteById(id);
    }
}