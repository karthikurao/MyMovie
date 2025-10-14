package com.moviebooking.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.moviebooking.entity.Customer;
import com.moviebooking.entity.User;
import com.moviebooking.service.ICustomerService;
import com.moviebooking.service.IUserService;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private IUserService userService;

    @Autowired
    private ICustomerService customerService;

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> addNewUser(@RequestBody User user) {
        try {
            User newUser = userService.addNewUser(user);
            return new ResponseEntity<>(Map.of(
                    "userId", newUser.getUserId(),
                    "email", newUser.getEmail(),
                    "role", newUser.getRole()
            ), HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.BAD_REQUEST);
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
    public ResponseEntity<Map<String, Object>> signInLegacy(@RequestBody User user) {
        try {
            User authenticatedUser = userService.signIn(user);
            return new ResponseEntity<>(Map.of(
                    "userId", authenticatedUser.getUserId(),
                    "email", authenticatedUser.getEmail(),
                    "role", authenticatedUser.getRole(),
                    "success", true
            ), HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(Map.of("error", "Invalid credentials"), HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping("/signout")
    public ResponseEntity<Map<String, Object>> signOut(@RequestBody User user) {
        try {
            User signedOutUser = userService.signOut(user);
            return new ResponseEntity<>(Map.of(
                    "userId", signedOutUser.getUserId(),
                    "email", signedOutUser.getEmail(),
                    "role", signedOutUser.getRole(),
                    "success", true
            ), HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }
}
