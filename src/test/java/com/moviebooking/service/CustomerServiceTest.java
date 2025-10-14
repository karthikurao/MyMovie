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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.moviebooking.entity.Customer;
import com.moviebooking.repository.ICustomerRepository;
import com.moviebooking.service.impl.CustomerServiceImpl;

@ExtendWith(MockitoExtension.class)
@DisplayName("Customer Service Tests")
public class CustomerServiceTest {

    @Mock
    private ICustomerRepository customerRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private CustomerServiceImpl customerService;

    private Customer testCustomer;

    @BeforeEach
    void setUp() {
        testCustomer = new Customer("John Doe", "123 Main St", "1234567890",
                "john@example.com", "password123");
        testCustomer.setCustomerId(1);
    }

    @Test
    @DisplayName("Should add customer successfully")
    void testAddCustomer_Success() {
        // Given
        when(customerRepository.existsByEmail(testCustomer.getEmail())).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPass");
        when(customerRepository.save(any(Customer.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Customer result = customerService.addCustomer(testCustomer);

        // Then
        assertNotNull(result);
        assertEquals(testCustomer.getCustomerName(), result.getCustomerName());
        assertEquals(testCustomer.getEmail(), result.getEmail());
        assertEquals("encodedPass", result.getPassword());
        verify(passwordEncoder).encode("password123");
        verify(customerRepository).save(any(Customer.class));
    }

    @Test
    @DisplayName("Should throw exception when customer email already exists")
    void testAddCustomer_EmailExists() {
        // Given
        when(customerRepository.existsByEmail(testCustomer.getEmail())).thenReturn(true);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> customerService.addCustomer(testCustomer));

        assertTrue(exception.getMessage().contains("already exists"));
        verify(customerRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should update customer successfully")
    void testUpdateCustomer_Success() {
        // Given
        Customer persisted = new Customer("Jane Doe", "456 Oak Ave", "0987654321",
                "jane@example.com", "$2a$10$abcdef");
        persisted.setCustomerId(testCustomer.getCustomerId());

        when(customerRepository.findById(testCustomer.getCustomerId())).thenReturn(Optional.of(persisted));
        when(passwordEncoder.encode("password123")).thenReturn("reencoded");
        when(customerRepository.save(any(Customer.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Customer updateRequest = new Customer("John Smith", "789 Pine Rd", "9998887777",
                "john@example.com", "password123");
        updateRequest.setCustomerId(testCustomer.getCustomerId());

        // When
        Customer result = customerService.updateCustomer(updateRequest);

        // Then
        assertNotNull(result);
        assertEquals(updateRequest.getCustomerId(), result.getCustomerId());
        assertEquals("reencoded", result.getPassword());
        assertEquals("John Smith", result.getCustomerName());
        verify(passwordEncoder).encode("password123");
        verify(customerRepository).save(any(Customer.class));
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent customer")
    void testUpdateCustomer_NotFound() {
        // Given
        when(customerRepository.findById(testCustomer.getCustomerId())).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> customerService.updateCustomer(testCustomer));

        assertTrue(exception.getMessage().contains("not found"));
    }

    @Test
    @DisplayName("Should view customer successfully")
    void testViewCustomer_Success() {
        // Given
        when(customerRepository.findById(1)).thenReturn(Optional.of(testCustomer));

        // When
        Customer result = customerService.viewCustomer(1);

        // Then
        assertNotNull(result);
        assertEquals(testCustomer.getCustomerId(), result.getCustomerId());
    }

    @Test
    @DisplayName("Should throw exception when viewing non-existent customer")
    void testViewCustomer_NotFound() {
        // Given
        when(customerRepository.findById(1)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> customerService.viewCustomer(1));

        assertTrue(exception.getMessage().contains("not found"));
    }
}
