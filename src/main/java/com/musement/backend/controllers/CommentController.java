package com.musement.backend.controllers;

import com.musement.backend.dto.CommentResponceDTO;
import com.musement.backend.services.CommentService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comment")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }


    @GetMapping("/getAll/{user_id}/{concert_id}")
    public List<CommentResponceDTO> getConcertComments(
            @PathVariable("user_id") Long userId,
            @PathVariable("concert_id") Long concertId,
            @RequestParam("tags") List<String> tags
    ) {
        return commentService.getConcertComments(userId, concertId, tags);
    }
}
