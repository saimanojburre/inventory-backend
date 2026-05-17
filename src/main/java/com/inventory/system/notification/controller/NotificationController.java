package com.inventory.system.notification.controller;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.inventory.system.notification.entity.Notification;
import com.inventory.system.notification.service.NotificationService;
import com.inventory.system.user.entity.User;
import com.inventory.system.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    private final UserRepository userRepository;

    /* =====================================================
       GET CURRENT USER
    ===================================================== */

    private User getCurrentUser(
            Authentication authentication
    ) {

        return (User) authentication.getPrincipal();
    }

    /* =====================================================
       GET USER NOTIFICATIONS
    ===================================================== */

    @GetMapping
    public List<Notification> getNotifications(
            Authentication authentication
    ) {

        User user =
                getCurrentUser(authentication);

        if (user == null) {

            return List.of();
        }

        return notificationService
                .getUserNotifications(
                        user.getId()
                );
    }

    /* =====================================================
       GET UNREAD COUNT
    ===================================================== */

    @GetMapping("/unread-count")
    public long getUnreadCount(
            Authentication authentication
    ) {

        User user =
                getCurrentUser(authentication);

        if (user == null) {

            return 0;
        }

        return notificationService
                .getUnreadCount(
                        user.getId()
                );
    }

    /* =====================================================
       MARK AS READ
    ===================================================== */

    @PutMapping("/{id}/read")
    public void markAsRead(
            @PathVariable Long id
    ) {

        notificationService.markAsRead(id);
    }

    /* =====================================================
   DELETE NOTIFICATION
===================================================== */

    @DeleteMapping("/{id}")
    public void deleteNotification(
            @PathVariable Long id
    ) {

        notificationService.deleteNotification(id);
    }

    /* =====================================================
   CLEAR ALL NOTIFICATIONS
===================================================== */

    @DeleteMapping
    public void clearAllNotifications(
            Authentication authentication
    ) {

        User user =
                getCurrentUser(authentication);

        notificationService.clearAllNotifications(
                user.getId()
        );
    }
}