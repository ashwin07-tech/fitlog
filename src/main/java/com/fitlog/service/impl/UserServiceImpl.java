package com.fitlog.service.impl;

import com.fitlog.entity.User;
import com.fitlog.exception.ResourceNotFoundException;
import com.fitlog.repository.UserRepository;
import com.fitlog.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User getOrCreateDefaultUser() {
        return userRepository.findByEmail("alex.runner@example.com")
                .orElseGet(() -> {
                    User defaultUser = new User("alex_runner", "alex.runner@example.com");
                    return userRepository.save(defaultUser);
                });
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email != null ? email.trim().toLowerCase() : "");
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username != null ? username.trim() : "");
    }

    @Override
    @Transactional(readOnly = true)
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    @Override
    public User createUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public User updateProfile(Long userId, String name, Double currentWeight, java.time.LocalDate birthDate) {
        User user = getUserById(userId);
        if (name != null && !name.trim().isEmpty()) {
            user.setName(name.trim());
        }
        if (currentWeight != null && currentWeight > 0) {
            user.setCurrentWeight(currentWeight);
        }
        if (birthDate != null) {
            user.setBirthDate(birthDate);
        }
        return userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public long getTotalUsersCount() {
        return userRepository.count();
    }
}
