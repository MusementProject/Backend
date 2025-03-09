package com.musement.backend.repositories;

import com.musement.backend.models.ArtistStatistics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArtistStatisticsRepository extends JpaRepository<ArtistStatistics, Long> {
    ArtistStatistics findByUserIdAndArtistId(Long userId, Long artistId);
}
