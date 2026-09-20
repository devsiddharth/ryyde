package com.ryyde.user.model;

import java.time.Instant;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import lombok.Data;

@Data
@Document(collection = "users")
public class User {

    @MongoId
    private String id;

    private String name;

    private String email;

    private String phone;

    private String passwordHash;

    private Double rating;

    private Instant createdAt;
}
