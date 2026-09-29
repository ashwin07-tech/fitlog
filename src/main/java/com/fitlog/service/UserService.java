package com.fitlog.service;

import com.fitlog.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    User getOrCreateDefaultUser();
    User getUserById(Long id);
    User createUser(User user);
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
    User updateProfile(Long userId, String name, Double currentWeight, java.time.LocalDate birthDate);
    List<User> getAllUsers();
    long getTotalUsersCount();
}
