package com.musement.backend.services;

import com.musement.backend.dto.AddCommentRequest;
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

    private User getUser (Long userId){
        Optional<User> user = userService.getUserById(userId);
        if (user.isEmpty()){
            throw new UserNotFoundException(userId);
        }
        return user.get();
    }

    private Concert getConcert(Long concertId){
        return concertService.getConcertById(concertId);
    }

    public boolean isVisitor (Long userId, Long concertId){
        User user = getUser(userId);
        Concert concert  = getConcert(concertId);
        if (!user.getAttendingConcerts().contains(concert)){
            return false;
        }
        return true;
    }

    public List<CommentResponceDTO> getConcertComments(Long userId, Long concertId){
        if (!isVisitor(userId, concertId)){
            throw new ConcertCommentIsNotAvailable(userId, concertId);
        }

        List<Comment> comments = null;
        comments = commentsRepository.getConcertAllComments(concertId);
        List<CommentResponceDTO> response = new ArrayList<>();
        for (Comment comment : comments){
            response.add(new CommentResponceDTO(comment));
        }
        return response;
    }

    public CommentResponceDTO addComment(AddCommentRequest addCommentRequest){
        if (isVisitor(addCommentRequest.getUserId(), addCommentRequest.getConcertId())){
            throw new ConcertCommentIsNotAvailable(addCommentRequest.getUserId(), addCommentRequest.getConcertId());
        }
        User user = getUser(addCommentRequest.getUserId());
        Concert concert = getConcert(addCommentRequest.getConcertId());
        Comment comment = new Comment();
        comment.setConcert(concert);
        comment.setUser(user);
        comment.setMessage(addCommentRequest.getMessage());
        comment.setTime(addCommentRequest.getTime());
        comment.setTags(addCommentRequest.getTags());
        commentsRepository.save(comment);
        return new CommentResponceDTO(comment);
    }
}
