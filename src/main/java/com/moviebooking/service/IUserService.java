package com.moviebooking.service;

import com.moviebooking.entity.User;

public interface IUserService {
    User addNewUser(User user);
    User signIn(User user);
    User signOut(User user);
    User signInByCredentials(String email, String password);
}
