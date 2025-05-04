package com.musement.backend.controllers;

import com.musement.backend.dto.FriendDTO;
import com.musement.backend.services.FriendshipService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/friends")
public class FriendshipController {

    private final FriendshipService friendshipService;

    public FriendshipController(FriendshipService friendshipService) {
        this.friendshipService = friendshipService;
    }

    @GetMapping("/getAll/{user_id}")
    public List<FriendDTO> getAllFriends(@PathVariable("user_id") Long userId){
        return friendshipService.getAllUserFriend(userId);
    }

    @GetMapping("/get/{user_id}/{friend_id}")
    public FriendDTO getFriend(@PathVariable("user_id") Long userId, @PathVariable("friend_id") Long friendId){
        return friendshipService.getFriend(userId, friendId);
    }

    @PostMapping("/add/{user_id}/{friend_id}")
    public FriendDTO addFriend(@PathVariable("user_id") Long userId, @PathVariable("friend_id") Long friendId){
        return friendshipService.addFriend(userId, friendId);
    }

    @DeleteMapping("/delete")
    public FriendDTO deleteFriend(){return null;}

}
