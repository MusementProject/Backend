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

    public List<FriendDTO> getAllUserFriend(Long userId){
        if (userService.getUserById(userId).isEmpty()){
            throw new UserNotFoundException(userId);
        }
        List<Friendship> friendships = friendshipRepository.findAllUserFriends(userId);
        List<FriendDTO> friendsInfo = new ArrayList<>();
        for (Friendship friendship : friendships){
            User newFriend = friendship.getFriend();
            friendsInfo.add(new FriendDTO(newFriend.getId(), newFriend.getUsername(), newFriend.getNickname(), newFriend.getProfilePicture(), true));
        }
        return friendsInfo;
    }

    public FriendDTO getFriend(Long userId, Long friendId){
        if (userService.getUserById(userId).isEmpty()){
            throw new UserNotFoundException(userId);
        }
        if (userService.getUserById(friendId).isEmpty()){
            throw new UserNotFoundException(friendId);
        }
        Optional<Friendship> friendship = friendshipRepository.findFriends(userId, friendId);
        if (friendship.isEmpty()){
            throw new FriendsNotFound(userId, friendId);
        }
        User friend = friendship.get().getFriend();
        return new FriendDTO(friend.getId(), friend.getUsername(), friend.getNickname(), friend.getProfilePicture(), true);
    }

    private FriendDTO addNewRequest(Long userId, Long friendId){
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
    public FriendDTO acceptFriend(Long userId, Long friendId){
        friendshipRepository.acceptRequest(friendId, userId);
        Friendship newFriendship = new Friendship();
        User user = userService.getUserById(userId).get();
        User friend = userService.getUserById(friendId).get();
        newFriendship.setUser(user);
        newFriendship.setFriend(friend);
        newFriendship.setAccepted(true);
        friendshipRepository.save(newFriendship);
        return new FriendDTO(friend.getId(), friend.getUsername(), friend.getNickname(), friend.getProfilePicture(), true);
    }

    public FriendDTO addFriend(Long userId, Long friendId){
        if (userService.getUserById(userId).isEmpty()){
            throw new UserNotFoundException(userId);
        }
        if (userService.getUserById(friendId).isEmpty()){
            throw new UserNotFoundException(friendId);
        }
        if (friendshipRepository.findRequest(friendId, userId).isEmpty()) {
            return addNewRequest(userId, friendId);
        }else{
            return acceptFriend(userId, friendId);
        }
    }
}
