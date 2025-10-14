package com.moviebooking.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.moviebooking.entity.User;
import com.moviebooking.repository.IUserRepository;
import com.moviebooking.service.IUserService;

@Service
public class UserServiceImpl implements IUserService {

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public User addNewUser(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new RuntimeException("Email is required");
        }

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("User with email " + user.getEmail() + " already exists");
        }

        if (user.getPassword() == null || user.getPassword().isBlank()) {
            throw new RuntimeException("Password is required");
        }

        user.setRole(user.getRole() != null ? user.getRole().toUpperCase() : "USER");
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Override
    public User signIn(User user) {
        if ((user.getEmail() == null || user.getEmail().isBlank()) && user.getUserId() <= 0) {
            throw new RuntimeException("Email or userId must be provided");
        }

        Optional<User> existingUser = Optional.empty();
        if (user.getEmail() != null && !user.getEmail().isBlank()) {
            existingUser = userRepository.findByEmail(user.getEmail());
        } else if (user.getUserId() > 0) {
            existingUser = userRepository.findById(user.getUserId());
        }

        User persisted = existingUser.orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!passwordEncoder.matches(user.getPassword(), persisted.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        return persisted;
    }

    @Override
    public User signOut(User user) {
        // In a real application, this might involve session management
        // For now, we'll just return the user
        return user;
    }

    @Override
    public User signInByCredentials(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Invalid admin credentials"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid admin credentials");
        }

        return user;
    }
}
