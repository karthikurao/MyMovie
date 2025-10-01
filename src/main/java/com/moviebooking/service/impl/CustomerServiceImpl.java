package com.moviebooking.service.impl;

import com.moviebooking.entity.Customer;
import com.moviebooking.repository.ICustomerRepository;
import com.moviebooking.service.ICustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class CustomerServiceImpl implements ICustomerService {

    @Autowired
    private ICustomerRepository customerRepository;

    @Override
    public Customer addCustomer(Customer customer) {
        if (customerRepository.existsByEmail(customer.getEmail())) {
            throw new RuntimeException("Customer with email " + customer.getEmail() + " already exists");
        }
        return customerRepository.save(customer);
    }

    @Override
    public Customer updateCustomer(Customer customer) {
        Optional<Customer> existingCustomer = customerRepository.findById(customer.getCustomerId());
        if (existingCustomer.isPresent()) {
            return customerRepository.save(customer);
        } else {
            throw new RuntimeException("Customer not found with ID: " + customer.getCustomerId());
        }
    }

    @Override
    public Customer deleteCustomer(Customer customer) {
        Optional<Customer> existingCustomer = customerRepository.findById(customer.getCustomerId());
        if (existingCustomer.isPresent()) {
            customerRepository.delete(customer);
            return customer;
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
        return customerRepository.findByEmailAndPassword(email, password)
            .orElseThrow(() -> new RuntimeException("Invalid email or password"));
    }

    @Override
    public Customer findByEmail(String email) {
        return customerRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Customer not found with email: " + email));
    }
}
