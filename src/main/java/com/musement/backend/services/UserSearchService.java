package com.musement.backend.services;

import com.musement.backend.documents.UserDocument;
import com.musement.backend.repositories.UserSearchRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserSearchService {
    private final UserSearchRepository userSearchRepository;


    @PostConstruct
    public void clearUserIndex() {
        userSearchRepository.deleteAll(); // для тестирования
    }

    public UserSearchService(UserSearchRepository userSearchRepository) {
        this.userSearchRepository = userSearchRepository;
    }

    public List<UserDocument> searchByUsername(String query){
        return userSearchRepository.findByUsernameContainingIgnoreCase(query);
    }
}
