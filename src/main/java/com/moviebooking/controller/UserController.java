package com.moviebooking.controller;

import com.moviebooking.entity.User;
import com.moviebooking.entity.Customer;
import com.moviebooking.service.IUserService;
import com.moviebooking.service.ICustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private IUserService userService;

    @Autowired
    private ICustomerService customerService;

    @PostMapping("/register")
    public ResponseEntity<User> addNewUser(@RequestBody User user) {
        try {
            User newUser = userService.addNewUser(user);
            return new ResponseEntity<>(newUser, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/signin")
    public ResponseEntity<Map<String, Object>> signIn(@RequestBody Map<String, String> loginRequest) {
        try {
            String email = loginRequest.get("email");
            String password = loginRequest.get("password");

            if (email == null || password == null) {
                return new ResponseEntity<>(Map.of("error", "Email and password are required"), HttpStatus.BAD_REQUEST);
            }

            // Try to authenticate as customer first (most common case)
            try {
                Customer customer = customerService.findByEmailAndPassword(email, password);
                if (customer != null) {
                    return new ResponseEntity<>(Map.of(
                        "userId", customer.getCustomerId(),
                        "email", customer.getEmail(),
                        "name", customer.getCustomerName(),
                        "role", "CUSTOMER",
                        "success", true
                    ), HttpStatus.OK);
                }
            } catch (Exception e) {
                // Continue to check admin/user table
            }

            // If not found as customer, try the user table for admin
            User authenticatedUser = userService.signInByCredentials(email, password);
            return new ResponseEntity<>(Map.of(
                "userId", authenticatedUser.getUserId(),
                "email", email,
                "role", authenticatedUser.getRole(),
                "success", true
            ), HttpStatus.OK);

        } catch (RuntimeException e) {
            return new ResponseEntity<>(Map.of("error", "Invalid email or password"), HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping("/signin-legacy")
    public ResponseEntity<User> signInLegacy(@RequestBody User user) {
        try {
            User authenticatedUser = userService.signIn(user);
            return new ResponseEntity<>(authenticatedUser, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping("/signout")
    public ResponseEntity<User> signOut(@RequestBody User user) {
        try {
            User signedOutUser = userService.signOut(user);
            return new ResponseEntity<>(signedOutUser, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }
}
