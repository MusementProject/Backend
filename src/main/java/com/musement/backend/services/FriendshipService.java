package com.musement.backend.services;

import com.musement.backend.dto.FriendDTO;
import com.musement.backend.exceptions.FriendsNotFound;
import com.musement.backend.exceptions.UserNotFoundException;
import com.musement.backend.models.Friendship;
import com.musement.backend.models.User;
import com.musement.backend.repositories.FriendshipRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class FriendshipService {
    private final FriendshipRepository friendshipRepository;
    private final UserService userService;

    public FriendshipService(FriendshipRepository friendshipRepository, UserService userService) {
        this.friendshipRepository = friendshipRepository;
        this.userService = userService;
    }

    private List<FriendDTO> friendshipsToFriendDTO(List<Friendship> friendships) {
        List<FriendDTO> friendsInfo = new ArrayList<>();
        for (Friendship friendship : friendships) {
            User newFriend = friendship.getFriend();
            friendsInfo.add(new FriendDTO(newFriend.getId(), newFriend.getUsername(), newFriend.getNickname(), newFriend.getProfilePicture(), true));
        }
        return friendsInfo;
    }

    public List<FriendDTO> getAllUserFriend(Long userId) {
        if (userService.getUserById(userId).isEmpty()) {
            throw new UserNotFoundException(userId);
        }
        List<Friendship> friendships = friendshipRepository.findAllUserFriends(userId);

        return friendshipsToFriendDTO(friendships);
    }


    public FriendDTO getFriend(Long userId, Long friendId) {
        if (userService.getUserById(userId).isEmpty()) {
            throw new UserNotFoundException(userId);
        }
        if (userService.getUserById(friendId).isEmpty()) {
            throw new UserNotFoundException(friendId);
        }
        Optional<Friendship> friendship = friendshipRepository.findFriends(userId, friendId);
        if (friendship.isEmpty()) {
            throw new FriendsNotFound(userId, friendId);
        }
        User friend = friendship.get().getFriend();
        return new FriendDTO(friend.getId(), friend.getUsername(), friend.getNickname(), friend.getProfilePicture(), true);
    }

    public Boolean getPair(Long userId, Long friendId) {
        if (userService.getUserById(userId).isEmpty()) {
            throw new UserNotFoundException(userId);
        }
        if (userService.getUserById(friendId).isEmpty()) {
            throw new UserNotFoundException(friendId);
        }
        Optional<Friendship> friendship = friendshipRepository.findPair(userId, friendId);
        return friendship.isPresent();
    }

    public List<FriendDTO> getFollowers(Long userId) {
        if (userService.getUserById(userId).isEmpty()) {
            throw new UserNotFoundException(userId);
        }
        List<Friendship> followers = friendshipRepository.getAllFollowers(userId);
        List<FriendDTO> friendsInfo = new ArrayList<>();
        for (Friendship friendship : followers) {
            User newFriend = friendship.getUser();
            friendsInfo.add(new FriendDTO(newFriend.getId(), newFriend.getUsername(), newFriend.getNickname(), newFriend.getProfilePicture(), false));
        }
        return friendsInfo;
    }

    public List<FriendDTO> getFollowing(Long userId) {
        if (userService.getUserById(userId).isEmpty()) {
            throw new UserNotFoundException(userId);
        }
        List<Friendship> followers = friendshipRepository.getAllFollowing(userId);
        return friendshipsToFriendDTO(followers);
    }

    private FriendDTO addNewRequest(Long userId, Long friendId) {
        Friendship newFriendsRequest = new Friendship();
        User user = userService.getUserById(userId).get();
        User friend = userService.getUserById(friendId).get();
        newFriendsRequest.setUser(user);
        newFriendsRequest.setFriend(friend);
        newFriendsRequest.setAccepted(false);
        friendshipRepository.save(newFriendsRequest);
        return new FriendDTO(friend.getId(), friend.getUsername(), friend.getNickname(), friend.getProfilePicture(), false);
    }


    @Transactional
    private FriendDTO acceptFriend(Long userId, Long friendId) {
        friendshipRepository.setFriendshipStatus(friendId, userId, true);
        Friendship newFriendship = new Friendship();
        User user = userService.getUserById(userId).get();
        User friend = userService.getUserById(friendId).get();
        newFriendship.setUser(user);
        newFriendship.setFriend(friend);
        newFriendship.setAccepted(true);
        friendshipRepository.save(newFriendship);
        return new FriendDTO(friend.getId(), friend.getUsername(), friend.getNickname(), friend.getProfilePicture(), true);
    }


    public FriendDTO addFriend(Long userId, Long friendId) {
        if (userService.getUserById(userId).isEmpty()) {
            throw new UserNotFoundException(userId);
        }
        if (userService.getUserById(friendId).isEmpty()) {
            throw new UserNotFoundException(friendId);
        }

        Optional<Friendship> possibleFriendship = friendshipRepository.findFriends(userId, friendId);
        if (possibleFriendship.isPresent()) {
            User friend = possibleFriendship.get().getFriend();
            return new FriendDTO(friend.getId(), friend.getUsername(), friend.getNickname(), friend.getProfilePicture(), possibleFriendship.get().isAccepted());
        }
        if (friendshipRepository.findRequest(friendId, userId).isEmpty()) {
            return addNewRequest(userId, friendId);
        } else {
            return acceptFriend(userId, friendId);
        }
    }

    @Transactional
    public Boolean deleteFriend(Long userId, Long friendId) {
        if (userService.getUserById(userId).isEmpty()) {
            throw new UserNotFoundException(userId);
        }
        if (userService.getUserById(friendId).isEmpty()) {
            throw new UserNotFoundException(friendId);
        }
        if (friendshipRepository.findRecord(userId, friendId).isEmpty()) {
            throw new FriendsNotFound(userId, friendId);
        }
        friendshipRepository.deleteFriend(userId, friendId);
        if (friendshipRepository.findRecord(friendId, userId).isPresent()) {
            friendshipRepository.setFriendshipStatus(friendId, userId, false);
        }
        return true;
    }
}
