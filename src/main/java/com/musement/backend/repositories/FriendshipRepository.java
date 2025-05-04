package com.musement.backend.repositories;

import com.musement.backend.models.Concert;
import com.musement.backend.models.Friendship;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendshipRepository extends JpaRepository<Friendship, Long> {
    @Query("SELECT f FROM Friendship f WHERE " +
            "f.user.id = :id AND f.accepted = true")
    List<Friendship> findAllUserFriends(@Param("id") Long id);

    @Query("SELECT f FROM Friendship f WHERE " +
            "(f.user.id = :userId AND f.friend.id = :friendId AND f.accepted = true)")
    Optional<Friendship> findFriends(@Param("userId") Long userId, @Param("friendId") Long friendId);

    @Query("SELECT f FROM Friendship f WHERE " +
           "(f.user.id = :userId AND f.friend.id = :friendId AND f.accepted = false)")
    Optional<Friendship> findRequest(@Param("userId") Long userId, @Param("friendId") Long friendId);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query("UPDATE Friendship f SET f.accepted = true WHERE " +
            "f.user.id = :userId AND f.friend.id = :friendId")
    int acceptRequest(@Param("userId") Long userId, @Param("friendId") Long friendId);
}
