package com.musement.backend.repositories;

import com.musement.backend.models.PlaylistArtistStat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlaylistArtistStatRepository extends JpaRepository<PlaylistArtistStat, Long> {
}