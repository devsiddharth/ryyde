package com.ryyde.user.service.implementations;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ryyde.common.exception.ResourceNotFoundException;
import com.ryyde.user.model.User;
import com.ryyde.user.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId("user-1");
        user.setName("Siddharth");
        user.setEmail("sid@example.com");
        user.setPhone("9999999999");
        user.setPasswordHash("hash");
        user.setRating(4.8);
    }

    @Test
    void createUserSavesAndReturnsUser() {
        when(userRepository.save(user)).thenReturn(user);

        User result = userService.createUser(user);

        assertSame(user, result);
        verify(userRepository).save(user);
    }

    @Test
    void getUserByIdReturnsUserWhenFound() {
        when(userRepository.findById("user-1")).thenReturn(Optional.of(user));

        User result = userService.getUserById("user-1");

        assertSame(user, result);
    }

    @Test
    void getUserByIdThrowsWhenNotFound() {
        when(userRepository.findById("missing")).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> userService.getUserById("missing"));

        assertEquals("User not found with id: missing", ex.getMessage());
    }

    @Test
    void getAllUsersReturnsAllUsers() {
        when(userRepository.findAll()).thenReturn(List.of(user));

        List<User> result = userService.getAllUsers();

        assertEquals(1, result.size());
        assertSame(user, result.get(0));
    }

    @Test
    void updateUserOverwritesOnlyNonNullFields() {
        User existing = new User();
        existing.setId("user-1");
        existing.setName("Old Name");
        existing.setEmail("old@example.com");
        existing.setPhone("1111111111");
        existing.setPasswordHash("old-hash");
        existing.setRating(3.5);

        User changes = new User();
        changes.setName("New Name");
        changes.setEmail("new@example.com");
        // phone, passwordHash and rating left null: must stay untouched

        when(userRepository.findById("user-1")).thenReturn(Optional.of(existing));
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        User result = userService.updateUser("user-1", changes);

        assertEquals("New Name", result.getName());
        assertEquals("new@example.com", result.getEmail());
        assertEquals("1111111111", result.getPhone());
        assertEquals("old-hash", result.getPasswordHash());
        assertEquals(3.5, result.getRating());
    }

    @Test
    void updateUserThrowsWhenUserNotFound() {
        when(userRepository.findById("missing")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> userService.updateUser("missing", new User()));
    }

    @Test
    void deleteUserLoadsThenDeletes() {
        when(userRepository.findById("user-1")).thenReturn(Optional.of(user));

        userService.deleteUser("user-1");

        verify(userRepository).delete(user);
    }

    @Test
    void deleteUserThrowsWhenNotFound() {
        when(userRepository.findById("missing")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.deleteUser("missing"));
        verify(userRepository, never()).delete(any(User.class));
    }

    @Test
    void getUserByEmailMatchesIgnoringCaseAndReturnsFirstMatch() {
        User other = new User();
        other.setId("user-2");
        other.setEmail("SID@example.com");

        when(userRepository.findAll()).thenReturn(List.of(other, user));

        // case-insensitive match; the first matching user in the list wins
        User result = userService.getUserByEmail("sid@example.com");

        assertSame(other, result);
    }

    @Test
    void getUserByEmailThrowsWhenNotFound() {
        when(userRepository.findAll()).thenReturn(List.of());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> userService.getUserByEmail("nobody@example.com"));

        assertEquals("User not found with email: nobody@example.com", ex.getMessage());
    }
}
