package com.inventory.system.notification.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.inventory.system.notification.entity.Notification;
import com.inventory.system.notification.repository.NotificationRepository;
import com.inventory.system.user.entity.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    /* =====================================================
       CREATE NOTIFICATION
    ===================================================== */

    public void createNotification(
            User user,
            String title,
            String message,
            String type
    ) {

        Notification notification = new Notification();

        notification.setUser(user);

        notification.setTitle(title);

        notification.setMessage(message);

        notification.setType(type);

        notificationRepository.save(notification);
    }

    /* =====================================================
       GET USER NOTIFICATIONS
    ===================================================== */

    public List<Notification> getUserNotifications(Long userId) {

        return notificationRepository
                .findByUserIdOrderByCreatedAtDesc(userId);
    }

    /* =====================================================
       UNREAD COUNT
    ===================================================== */

    public long getUnreadCount(Long userId) {

        return notificationRepository
                .countByUserIdAndReadStatusFalse(userId);
    }

    /* =====================================================
       MARK AS READ
    ===================================================== */

    public void markAsRead(Long notificationId) {

        Notification notification =
                notificationRepository.findById(notificationId)
                        .orElseThrow(() ->
                                new RuntimeException("Notification not found"));

        notification.setReadStatus(true);

        notificationRepository.save(notification);
    }

    /* =====================================================
   DELETE SINGLE NOTIFICATION
===================================================== */

    public void deleteNotification(
            Long notificationId
    ) {

        notificationRepository.deleteById(
                notificationId
        );
    }

/* =====================================================
   CLEAR ALL NOTIFICATIONS
===================================================== */

    public void clearAllNotifications(
            Long userId
    ) {

        notificationRepository.deleteByUserId(
                userId
        );
    }
}