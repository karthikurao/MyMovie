package com.moviebooking.service;

import com.moviebooking.entity.Customer;
import java.util.List;

public interface ICustomerService {
    Customer addCustomer(Customer customer);
    Customer updateCustomer(Customer customer);
    Customer deleteCustomer(Customer customer);
    Customer viewCustomer(int customerId);
    List<Customer> viewAllCustomers(int movieId);

    // New methods for email-based authentication
    Customer findByEmailAndPassword(String email, String password);
    Customer findByEmail(String email);
}
