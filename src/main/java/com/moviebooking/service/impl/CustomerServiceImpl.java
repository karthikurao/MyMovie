package com.moviebooking.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.moviebooking.entity.Customer;
import com.moviebooking.repository.ICustomerRepository;
import com.moviebooking.service.ICustomerService;

@Service
public class CustomerServiceImpl implements ICustomerService {

    @Autowired
    private ICustomerRepository customerRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public Customer addCustomer(Customer customer) {
        if (customerRepository.existsByEmail(customer.getEmail())) {
            throw new RuntimeException("Customer with email " + customer.getEmail() + " already exists");
        }
        if (customer.getPassword() != null && !customer.getPassword().startsWith("$2")) {
            customer.setPassword(passwordEncoder.encode(customer.getPassword()));
        }
        return customerRepository.save(customer);
    }

    @Override
    public Customer updateCustomer(Customer customer) {
        Optional<Customer> existingCustomer = customerRepository.findById(customer.getCustomerId());
        if (existingCustomer.isPresent()) {
            Customer persisted = existingCustomer.get();

            persisted.setCustomerName(customer.getCustomerName());
            persisted.setAddress(customer.getAddress());
            persisted.setMobileNumber(customer.getMobileNumber());
            persisted.setEmail(customer.getEmail());

            if (customer.getPassword() != null && !customer.getPassword().isBlank()) {
                String incomingPassword = customer.getPassword();
                if (!incomingPassword.startsWith("$2") || !incomingPassword.equals(persisted.getPassword())) {
                    persisted.setPassword(passwordEncoder.encode(incomingPassword));
                }
            }

            return customerRepository.save(persisted);
        } else {
            throw new RuntimeException("Customer not found with ID: " + customer.getCustomerId());
        }
    }

    @Override
    public Customer deleteCustomer(Customer customer) {
        Optional<Customer> existingCustomer = customerRepository.findById(customer.getCustomerId());
        if (existingCustomer.isPresent()) {
            customerRepository.delete(existingCustomer.get());
            return existingCustomer.get();
        } else {
            throw new RuntimeException("Customer not found with ID: " + customer.getCustomerId());
        }
    }

    @Override
    public Customer viewCustomer(int customerId) {
        return customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found with ID: " + customerId));
    }

    @Override
    public List<Customer> viewAllCustomers(int movieId) {
        // This method would need additional logic to filter by movie
        // For now, returning all customers
        return customerRepository.findAll();
    }

    @Override
    public Customer findByEmailAndPassword(String email, String password) {
        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        if (!passwordEncoder.matches(password, customer.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        return customer;
    }

    @Override
    public Customer findByEmail(String email) {
        return customerRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Customer not found with email: " + email));
    }
}
