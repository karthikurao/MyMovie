package com.moviebooking.service.impl;

import com.moviebooking.entity.User;
import com.moviebooking.repository.IUserRepository;
import com.moviebooking.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class UserServiceImpl implements IUserService {

    @Autowired
    private IUserRepository userRepository;

    @Override
    public User addNewUser(User user) {
        if (userRepository.existsByUserId(user.getUserId())) {
            throw new RuntimeException("User with ID " + user.getUserId() + " already exists");
        }
        return userRepository.save(user);
    }

    @Override
    public User signIn(User user) {
        Optional<User> existingUser = userRepository.findByUserIdAndPassword(
            user.getUserId(), user.getPassword());

        if (existingUser.isPresent()) {
            return existingUser.get();
        } else {
            throw new RuntimeException("Invalid credentials");
        }
    }

    @Override
    public User signOut(User user) {
        // In a real application, this might involve session management
        // For now, we'll just return the user
        return user;
    }

    @Override
    public User signInByCredentials(String email, String password) {
        // For admin users, we'll use a default admin account
        // In a real application, you might have an admin table with email/password
        if ("admin@mymovie.com".equals(email) && "admin123".equals(password)) {
            User adminUser = new User();
            adminUser.setUserId(1);
            adminUser.setRole("ADMIN");
            adminUser.setPassword(password);
            return adminUser;
        }

        throw new RuntimeException("Invalid admin credentials");
    }
}
