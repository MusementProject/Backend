package com.musement.backend.models;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(name = "images")
@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // URL from Cloudinary
    @Column(nullable = false, length = 1024)
    private String url;

    // time of upload (automatically generated)
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;
}