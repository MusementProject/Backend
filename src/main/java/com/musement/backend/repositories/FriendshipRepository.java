package com.musement.backend.repositories;

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

    @Query("SELECT f FROM Friendship f WHERE " +
            "(f.user.id = :userId AND f.friend.id = :friendId)")
    Optional<Friendship> findPair(@Param("userId") Long userId, @Param("friendId") Long friendId);

    @Query("SELECT f FROM Friendship f WHERE " +
            "f.user.id = :userId AND f.friend.id = :friendId")
    Optional<Friendship> findRecord(@Param("userId") Long userId, @Param("friendId") Long friendId);

    @Query("SELECT f FROM Friendship f WHERE " +
            "f.friend.id = :userId AND f.accepted = false")
    List<Friendship> getAllFollowers(@Param("userId") Long userId);

    @Query("SELECT f FROM Friendship f WHERE " +
            "f.user.id = :userId AND f.accepted = false")
    List<Friendship> getAllFollowing(@Param("userId") Long userId);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query("UPDATE Friendship f SET f.accepted = :status WHERE " +
            "f.user.id = :userId AND f.friend.id = :friendId")
    int setFriendshipStatus(@Param("userId") Long userId, @Param("friendId") Long friendId, @Param("status") boolean status);

    @Transactional
    @Modifying
    @Query("Delete FROM Friendship f WHERE " +
            "f.user.id = :userId AND f.friend.id = :friendId")
    int deleteFriend(@Param("userId") Long userId, @Param("friendId") Long friendId);
}
