package com.linkedin;

import com.linkedin.controller.NotificationController;
import com.linkedin.dto.response.ApiResponse;
import com.linkedin.dto.response.NotificationResponse;
import com.linkedin.service.NotificationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationControllerTest {

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private NotificationController notificationController;

    private UserDetails getUserDetails() {
        return User.withUsername("john.doe@example.com")
                .password("password")
                .authorities(Collections.emptyList())
                .build();
    }

    @Test
    void getNotifications_Success() {
        UserDetails userDetails = getUserDetails();
        Page<NotificationResponse> page = new PageImpl<>(List.of(new NotificationResponse()));

        when(notificationService.getNotifications(eq("john.doe@example.com"), any()))
                .thenReturn(page);

        ResponseEntity<ApiResponse<Page<NotificationResponse>>> response =
                notificationController.getNotifications(userDetails, 0, 20);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals("Notifications fetched", response.getBody().getMessage());
        assertEquals(page, response.getBody().getData());
    }

    @Test
    void getUnreadCount_Success() {
        UserDetails userDetails = getUserDetails();

        when(notificationService.getUnreadCount("john.doe@example.com")).thenReturn(5L);

        ResponseEntity<ApiResponse<Long>> response =
                notificationController.getUnreadCount(userDetails);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals("Unread count fetched", response.getBody().getMessage());
        assertEquals(5L, response.getBody().getData());
    }

    @Test
    void markAsRead_Success() {
        UserDetails userDetails = getUserDetails();

        doNothing().when(notificationService).markAsRead("john.doe@example.com", 1L);

        ResponseEntity<ApiResponse<Void>> response =
                notificationController.markAsRead(userDetails, 1L);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals("Notification marked as read", response.getBody().getMessage());
        verify(notificationService).markAsRead("john.doe@example.com", 1L);
    }

    @Test
    void markAllAsRead_Success() {
        UserDetails userDetails = getUserDetails();

        doNothing().when(notificationService).markAllAsRead("john.doe@example.com");

        ResponseEntity<ApiResponse<Void>> response =
                notificationController.markAllAsRead(userDetails);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals("All notifications marked as read", response.getBody().getMessage());
        verify(notificationService).markAllAsRead("john.doe@example.com");
    }
}