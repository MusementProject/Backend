package com.musement.backend.repositories;

import com.musement.backend.models.Comment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentsRepository extends JpaRepository<Comment, Long> {

    @Query("SELECT c FROM Comment WHERE " +
            "c.concert.id = :concertId")
    List<Comment> getConcertAllComments(@Param("concertId") Long concertId);

    @Query("SELECT DISTINCT c FROM Comment c JOIN c.tags t " +
            "WHERE c.concert.id = :concertId AND t IN :tags")
    List<Comment> findByConcertIdAndTagsIn(@Param("concertId") Long concertId, @Param("tags") List<String> tags);

}
