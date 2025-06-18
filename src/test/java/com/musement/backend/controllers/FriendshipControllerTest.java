//package com.musement.backend.controllers;
//
//import com.musement.backend.config.JwtTokenProvider;
//import com.musement.backend.dto.FriendDTO;
//import com.musement.backend.services.FriendshipService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Import;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.TestConfiguration;
//
//import java.util.List;
//
//import static org.mockito.Mockito.*;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@WebMvcTest(FriendshipController.class)
//@Import(FriendshipControllerTest.MockConfig.class)
//public class FriendshipControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Autowired
//    private FriendshipService friendshipService;
//
//    private FriendDTO friendDTO;
//
//    @TestConfiguration
//    static class MockConfig {
//        @Bean
//        public FriendshipService friendshipService() {
//            return mock(FriendshipService.class);
//        }
//        @Bean
//        public JwtTokenProvider jwtTokenProvider() {
//            return mock(JwtTokenProvider.class);
//        }
//    }
//
//    @BeforeEach
//    public void setup() {
//        friendDTO = new FriendDTO();
//        friendDTO.setId(1L);
//        friendDTO.setUsername("testuser");
//    }
//
//    @Test
//    public void testGetAllFriends() throws Exception {
//        when(friendshipService.getAllUserFriend(1L)).thenReturn(List.of(friendDTO));
//
//        mockMvc.perform(get("/api/friends/getAll/1"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$[0].id").value(friendDTO.getId()))
//                .andExpect(jsonPath("$[0].username").value(friendDTO.getUsername()));
//    }
//
//    @Test
//    public void testGetPair() throws Exception {
//        when(friendshipService.getPair(1L, 2L)).thenReturn(true);
//
//        mockMvc.perform(get("/api/friends/get/1/2"))
//                .andExpect(status().isOk())
//                .andExpect(content().string("true"));
//    }
//
//    @Test
//    public void testGetFollowers() throws Exception {
//        when(friendshipService.getFollowers(1L)).thenReturn(List.of(friendDTO));
//
//        mockMvc.perform(get("/api/friends/getFollowers/1"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$[0].id").value(friendDTO.getId()));
//    }
//
//    @Test
//    public void testGetFollowing() throws Exception {
//        when(friendshipService.getFollowing(1L)).thenReturn(List.of(friendDTO));
//
//        mockMvc.perform(get("/api/friends/getFollowing/1"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$[0].id").value(friendDTO.getId()));
//    }
//
//    @Test
//    public void testAddFriend() throws Exception {
//        when(friendshipService.addFriend(1L, 2L)).thenReturn(friendDTO);
//
//        mockMvc.perform(post("/api/friends/add/1/2"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id").value(friendDTO.getId()));
//    }
//
//    @Test
//    public void testDeleteFriend() throws Exception {
//        when(friendshipService.deleteFriend(1L, 2L)).thenReturn(true);
//
//        mockMvc.perform(delete("/api/friends/delete/1/2"))
//                .andExpect(status().isOk())
//                .andExpect(content().string("true"));
//    }
//}
