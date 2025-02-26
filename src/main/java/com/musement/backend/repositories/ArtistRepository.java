package com.musement.backend.repositories;

import com.musement.backend.models.Artist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ArtistRepository extends JpaRepository<Artist, Long> {
    Optional<Object> findArtistByName(String name);

    Optional<Artist> findArtistById(Long id);
}
