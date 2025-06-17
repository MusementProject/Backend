package com.musement.backend.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "concerts")
@Getter
@Setter
public class Concert {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @ManyToOne
    @JoinColumn(name = "artist_id", nullable = false)
    private Artist artist;

    @Column(nullable = false)
    private String location;

    @Column(nullable = false)
    private LocalDateTime date;

    @Column(name = "image_url")
    private String imageUrl;

    @ManyToMany(mappedBy = "attendingConcerts")
    @JsonIgnore
    private Set<User> attendees;

    public Concert() {}

    public Concert(String title, Artist artist, String location, LocalDateTime date, String imageUrl) {
        this.title = title;
        this.artist = artist;
        this.location = location;
        this.date = date;
        this.imageUrl = imageUrl;
    }
}
