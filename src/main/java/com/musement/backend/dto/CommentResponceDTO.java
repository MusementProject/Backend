package com.musement.backend.dto;

import com.musement.backend.models.Comment;
import com.musement.backend.models.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CommentResponceDTO {
    private User user;
    private String message;
    private Date time;
    private String attachedPicture;
    private List<String> tags;

    public CommentResponceDTO(Comment comment){
        this.user = comment.getUser();
        this.message = comment.getMessage();
        this.time = comment.getTime();
        this.attachedPicture = comment.getAttachedPicture();
        this.tags = comment.getTags();
    }
}
