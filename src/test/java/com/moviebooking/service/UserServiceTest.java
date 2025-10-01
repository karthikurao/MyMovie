package com.moviebooking.service;

import com.moviebooking.entity.User;
import com.moviebooking.repository.IUserRepository;
import com.moviebooking.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("User Service Tests")
public class UserServiceTest {

    @Mock
    private IUserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        testUser = new User("password123", "CUSTOMER");
    }

    @Test
    @DisplayName("Should add new user successfully")
    void testAddNewUser_Success() {
        // Given
        when(userRepository.existsByUserId(testUser.getUserId())).thenReturn(false);
        when(userRepository.save(testUser)).thenReturn(testUser);

        // When
        User result = userService.addNewUser(testUser);

        // Then
        assertNotNull(result);
        assertEquals(testUser.getPassword(), result.getPassword());
        assertEquals(testUser.getRole(), result.getRole());
        verify(userRepository).save(testUser);
    }

    @Test
    @DisplayName("Should throw exception when user already exists")
    void testAddNewUser_UserExists() {
        // Given
        when(userRepository.existsByUserId(testUser.getUserId())).thenReturn(true);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> userService.addNewUser(testUser));

        assertTrue(exception.getMessage().contains("already exists"));
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should sign in user with valid credentials")
    void testSignIn_ValidCredentials() {
        // Given
        when(userRepository.findByUserIdAndPassword(testUser.getUserId(), testUser.getPassword()))
            .thenReturn(Optional.of(testUser));

        // When
        User result = userService.signIn(testUser);

        // Then
        assertNotNull(result);
        assertEquals(testUser.getUserId(), result.getUserId());
    }

    @Test
    @DisplayName("Should throw exception for invalid credentials")
    void testSignIn_InvalidCredentials() {
        // Given
        when(userRepository.findByUserIdAndPassword(testUser.getUserId(), testUser.getPassword()))
            .thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> userService.signIn(testUser));

        assertEquals("Invalid credentials", exception.getMessage());
    }

    @Test
    @DisplayName("Should sign out user successfully")
    void testSignOut_Success() {
        // When
        User result = userService.signOut(testUser);

        // Then
        assertNotNull(result);
        assertEquals(testUser, result);
    }
}
