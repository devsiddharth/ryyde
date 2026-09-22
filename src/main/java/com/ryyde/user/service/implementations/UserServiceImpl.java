package com.ryyde.user.service.implementations;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ryyde.common.exception.ResourceNotFoundException;
import com.ryyde.user.model.User;
import com.ryyde.user.repository.UserRepository;
import com.ryyde.user.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public User createUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public User getUserById(String id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User updateUser(String id, User user) {
        User existingUser = getUserById(id);

        if (user.getName() != null) {
            existingUser.setName(user.getName());
        }
        if (user.getEmail() != null) {
            existingUser.setEmail(user.getEmail());
        }
        if (user.getPhone() != null) {
            existingUser.setPhone(user.getPhone());
        }
        if (user.getPasswordHash() != null) {
            existingUser.setPasswordHash(user.getPasswordHash());
        }
        if (user.getRating() != null) {
            existingUser.setRating(user.getRating());
        }

        return userRepository.save(existingUser);
    }

    @Override
    public void deleteUser(String id) {
        User existingUser = getUserById(id);
        userRepository.delete(existingUser);
    }

    @Override
    public User getUserByEmail(String email) {
        return userRepository.findAll().stream()
                .filter(user -> email.equalsIgnoreCase(user.getEmail()))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }
}
