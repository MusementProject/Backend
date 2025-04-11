package com.musement.backend.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String googleId;

    @Column(nullable = false)
    private String username;

    @Column
    private String nickname;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(columnDefinition = "TEXT")
    private String bio;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "profile_picture")
    private String profilePicture;

    @ManyToMany
    @JoinTable(
            name = "user_concerts",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "concert_id")
    )
    private Set<Concert> attendingConcerts = new HashSet<>();

}
