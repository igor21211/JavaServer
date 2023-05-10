package com.example.javaserver.facade;

import com.example.javaserver.dto.CommentDto;
import com.example.javaserver.entity.Comment;
import org.springframework.stereotype.Component;

@Component
public class CommentFacade {

    public CommentDto commentToCommentDto(Comment comment){
        CommentDto commentDto = new CommentDto();
        commentDto.setId(comment.getId());
        commentDto.setUsername(comment.getUsername());
        commentDto.setMessage(comment.getMessage());
        return commentDto;
    }
}
