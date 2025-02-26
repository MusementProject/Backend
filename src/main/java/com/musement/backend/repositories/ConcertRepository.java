package com.musement.backend.repositories;

import com.musement.backend.models.Concert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ConcertRepository extends JpaRepository<Concert, Long> {
    @Query("SELECT c FROM Concert c JOIN c.attendees u WHERE u.id = :userId")
    List<Concert> findByAttendee(@Param("userId") Long userId);

    @Query("SELECT c FROM Concert c WHERE " +
            "(:location IS NULL OR c.location = :location) AND " +
            "(:fromDate IS NULL OR c.date >= :fromDate) AND " +
            "(:toDate IS NULL OR c.date <= :toDate)")
    List<Concert> findByFilters(@Param("location") String location,
                                @Param("fromDate") LocalDate fromDate,
                                @Param("toDate") LocalDate toDate);
}
