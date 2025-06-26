package com.musement.backend.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Getter
@Setter
public class AddCommentRequest {
    private Long userId;
    private Long concertId;
    private String message;
    private Date time;
}
