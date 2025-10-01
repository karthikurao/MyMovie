package com.moviebooking.service;

import com.moviebooking.entity.Customer;
import com.moviebooking.repository.ICustomerRepository;
import com.moviebooking.service.impl.CustomerServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Customer Service Tests")
public class CustomerServiceTest {

    @Mock
    private ICustomerRepository customerRepository;

    @InjectMocks
    private CustomerServiceImpl customerService;

    private Customer testCustomer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        testCustomer = new Customer("John Doe", "123 Main St", "1234567890",
                                   "john@example.com", "password123");
        testCustomer.setCustomerId(1);
    }

    @Test
    @DisplayName("Should add customer successfully")
    void testAddCustomer_Success() {
        // Given
        when(customerRepository.existsByEmail(testCustomer.getEmail())).thenReturn(false);
        when(customerRepository.save(testCustomer)).thenReturn(testCustomer);

        // When
        Customer result = customerService.addCustomer(testCustomer);

        // Then
        assertNotNull(result);
        assertEquals(testCustomer.getCustomerName(), result.getCustomerName());
        assertEquals(testCustomer.getEmail(), result.getEmail());
        verify(customerRepository).save(testCustomer);
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
        when(customerRepository.findById(testCustomer.getCustomerId())).thenReturn(Optional.of(testCustomer));
        when(customerRepository.save(testCustomer)).thenReturn(testCustomer);

        // When
        Customer result = customerService.updateCustomer(testCustomer);

        // Then
        assertNotNull(result);
        assertEquals(testCustomer.getCustomerId(), result.getCustomerId());
        verify(customerRepository).save(testCustomer);
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
