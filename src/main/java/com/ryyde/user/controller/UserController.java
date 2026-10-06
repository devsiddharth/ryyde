package com.ryyde.user.controller;

import com.ryyde.user.model.User;
import com.ryyde.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users/")
@RequiredArgsConstructor

public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user){
        User createdUser = userService.createUser(user);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdUser);
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<User> getUserById(@PathVariable String id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<User> getUserByEmail(@PathVariable String email) {
        return ResponseEntity.ok(userService.getUserByEmail(email));
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers(){
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PatchMapping("/{id}")
    public ResponseEntity<User> updateUserById(@PathVariable String id, @RequestBody User user){
        User updatedUser = userService.updateUser(id, user);

        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUserById(@PathVariable String id){

        userService.deleteUser(id);

        return ResponseEntity.noContent().build();
    }

}