package com.moviebooking.service;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.moviebooking.entity.User;
import com.moviebooking.repository.IUserRepository;
import com.moviebooking.service.impl.UserServiceImpl;

@ExtendWith(MockitoExtension.class)
@DisplayName("User Service Tests")
public class UserServiceTest {

    @Mock
    private IUserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User("user@example.com", "password123", "customer");
    }

    @Test
    @DisplayName("Should add new user successfully")
    void testAddNewUser_Success() {
        // Given
        when(userRepository.existsByEmail(testUser.getEmail())).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPass");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        User result = userService.addNewUser(testUser);

        // Then
        assertNotNull(result);
        assertEquals("encodedPass", result.getPassword());
        assertEquals("CUSTOMER", result.getRole());
        verify(passwordEncoder).encode("password123");
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw exception when user already exists")
    void testAddNewUser_UserExists() {
        // Given
        when(userRepository.existsByEmail(testUser.getEmail())).thenReturn(true);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> userService.addNewUser(testUser));

        assertTrue(exception.getMessage().contains("already exists"));
        verify(userRepository, never()).save(any());
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    @DisplayName("Should sign in user with valid credentials")
    void testSignIn_ValidCredentials() {
        // Given
        User storedUser = new User(testUser.getEmail(), "encodedPass", "CUSTOMER");
        storedUser.setUserId(101);

        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(Optional.of(storedUser));
        when(passwordEncoder.matches("password123", "encodedPass")).thenReturn(true);

        // When
        User result = userService.signIn(testUser);

        // Then
        assertNotNull(result);
        assertEquals(101, result.getUserId());
        verify(passwordEncoder).matches("password123", "encodedPass");
    }

    @Test
    @DisplayName("Should throw exception for invalid credentials")
    void testSignIn_InvalidCredentials() {
        // Given
        User storedUser = new User(testUser.getEmail(), "encodedPass", "CUSTOMER");
        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(Optional.of(storedUser));
        when(passwordEncoder.matches("password123", "encodedPass")).thenReturn(false);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> userService.signIn(testUser));

        assertEquals("Invalid credentials", exception.getMessage());
        verify(passwordEncoder).matches("password123", "encodedPass");
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
