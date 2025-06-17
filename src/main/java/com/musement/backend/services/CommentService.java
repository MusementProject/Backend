package com.musement.backend.services;

import com.musement.backend.dto.CommentResponceDTO;
import com.musement.backend.exceptions.ConcertCommentIsNotAvailable;
import com.musement.backend.exceptions.UserNotFoundException;
import com.musement.backend.models.Comment;
import com.musement.backend.models.Concert;
import com.musement.backend.models.User;
import com.musement.backend.repositories.CommentsRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CommentService {

    private final UserService userService;
    private final ConcertService concertService;
    private final CommentsRepository commentsRepository;

    public CommentService(UserService userService, ConcertService concertService, CommentsRepository commentsRepository) {
        this.userService = userService;
        this.concertService = concertService;
        this.commentsRepository = commentsRepository;
    }

    public boolean isVisitor (Long userId, Long concertId){
        Optional<User> user = userService.getUserById(userId);
        if (user.isEmpty()){
            throw new UserNotFoundException(userId);
        }
        Concert concert  = concertService.getConcertById(concertId);
        if (!user.get().getAttendingConcerts().contains(concert)){
            return false;
        }
        return true;
    }

    public List<CommentResponceDTO> getConcertComments(Long userId, Long concertId, List<String> tags){
        if (!isVisitor(userId, concertId)){
            throw new ConcertCommentIsNotAvailable(userId, concertId);
        }

        List<Comment> comments = null;
        if (tags.isEmpty()){
            comments = commentsRepository.getConcertAllComments(concertId);
        }else{
            comments = commentsRepository.findByConcertIdAndTagsIn(concertId, tags);
        }
        List<CommentResponceDTO> response = new ArrayList<>();
        for (Comment comment : comments){
            response.add(new CommentResponceDTO(comment));
        }
        return response;
    }
}
