package com.musement.backend.services;

import com.musement.backend.documents.UserDocument;
import com.musement.backend.dto.RegistrationRequestDTO;
import com.musement.backend.dto.RegistrationResponseDTO;
import com.musement.backend.exceptions.UserAlreadyExistsException;
import com.musement.backend.models.User;
import com.musement.backend.repositories.UserRepository;
import com.musement.backend.repositories.UserSearchRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class RegistrationService {
    private final UserRepository userRepository;
    private final UserSearchRepository userSearchRepository;
    private final PasswordEncoder passwordEncoder;

    public RegistrationService(UserRepository userRepository, UserSearchRepository userSearchRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userSearchRepository = userSearchRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public RegistrationResponseDTO register(RegistrationRequestDTO request) throws UserAlreadyExistsException {
//        if (userRepository.findUserByUsername(request.getUsername()).isPresent()) {
//            throw new UserAlreadyExistsException("Username already exists");
//        }
        if (userRepository.findUserByEmail(request.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException("Email already exists");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);
        indexUser(user);
        RegistrationResponseDTO response = new RegistrationResponseDTO();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        return response;
    }

    private  void indexUser(User user){
        UserDocument doc = new UserDocument();
        doc.setId(user.getId());
        doc.setUsername(user.getUsername());
        doc.setNickname(user.getNickname());
        doc.setProfilePicture(user.getProfilePicture());

        try {
            userSearchRepository.save(doc);
            System.out.println("User indexed in Elasticsearch: " + doc.getUsername());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
