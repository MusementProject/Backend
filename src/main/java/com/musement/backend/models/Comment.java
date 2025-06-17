package com.musement.backend.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "comments")
@Getter
@Setter
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "concert_id", nullable = false)
    private Concert concert;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String message;

    @Column(nullable = false)
    private LocalDateTime time;

    @Column
    private String attachedPicture;

    @ElementCollection
    @CollectionTable(name = "comment_tags", joinColumns = @JoinColumn(name = "comment_id"))
    @Column(name = "tag")
    private List<String> tags;
}
