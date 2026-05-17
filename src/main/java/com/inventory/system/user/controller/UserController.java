package com.inventory.system.user.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.inventory.system.user.dto.CreateUserRequest;
import com.inventory.system.user.dto.UpdateUserRequest;
import com.inventory.system.user.entity.User;
import com.inventory.system.user.service.UserService;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(
            UserService userService
    ) {

        this.userService = userService;
    }

    // =====================================================
    // CREATE USER
    // =====================================================

    @PostMapping
    public ResponseEntity<User> createUser(

            @RequestBody CreateUserRequest request,

            @RequestHeader(
                    value = "X-Session-Id",
                    required = false
            )
            String sessionId
    ) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        userService.createUser(
                                request,
                                sessionId
                        )
                );
    }

    // =====================================================
    // GET ALL USERS
    // =====================================================

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {

        return ResponseEntity.ok(
                userService.getAllUsers()
        );
    }

    // =====================================================
    // GET USER BY ID
    // =====================================================

    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                userService.getUserById(id)
        );
    }

    // =====================================================
    // UPDATE USER
    // =====================================================

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(

            @PathVariable Long id,

            @RequestBody UpdateUserRequest request,

            @RequestHeader(
                    value = "X-Session-Id",
                    required = false
            )
            String sessionId
    ) {

        return ResponseEntity.ok(
                userService.updateUser(
                        id,
                        request,
                        sessionId
                )
        );
    }

    // =====================================================
    // DELETE USER
    // =====================================================

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(

            @PathVariable Long id,

            @RequestHeader(
                    value = "X-Session-Id",
                    required = false
            )
            String sessionId
    ) {

        userService.deleteUser(
                id,
                sessionId
        );

        return ResponseEntity.ok(
                "User deleted successfully"
        );
    }
}