package com.example.javaserver.web;

import com.example.javaserver.dto.CommentDto;
import com.example.javaserver.entity.Comment;
import com.example.javaserver.facade.CommentFacade;
import com.example.javaserver.payload.response.MessageResponse;
import com.example.javaserver.services.CommentService;
import com.example.javaserver.validations.ResponseErrorValidation;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.swing.*;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/comment")
@CrossOrigin
@AllArgsConstructor
public class CommentController {

    private CommentService commentService;
    private CommentFacade commentFacade;
    private ResponseErrorValidation responseErrorValidation;

    @PostMapping("/{postId}/create")
    public ResponseEntity<Object> createComment(@Valid @RequestBody CommentDto commentDto,
                                                @PathVariable("postId") String postId,
                                                BindingResult bindingResult,
                                                Principal principal){
        ResponseEntity<Object> errors = responseErrorValidation.mapValidationService(bindingResult);
        if(!ObjectUtils.isEmpty(errors)) return errors;

        Comment comment = commentService.saveComment(Long.parseLong(postId),commentDto, principal);
        CommentDto commentCreated = commentFacade.commentToCommentDto(comment);
        return new ResponseEntity<>(commentCreated, HttpStatus.OK);
    }
    @GetMapping("/{postId}/all")
    public ResponseEntity<List<CommentDto>> getAllCommentsToPost(@PathVariable("postId") String postId){
        List<CommentDto> commentDtoList = commentService.getAllCommentsForPost(Long.parseLong(postId))
                .stream()
                .map(commentFacade::commentToCommentDto)
                .collect(Collectors.toList());
        return new ResponseEntity<>(commentDtoList,HttpStatus.OK);
    }
    @PostMapping("/{commentId}/delete")
    public ResponseEntity<MessageResponse> deleteComment(@PathVariable("commentId") String commentId){
        commentService.deleteComment(Long.parseLong(commentId));
        return new ResponseEntity<>(new MessageResponse("Post was deleted"), HttpStatus.OK);

    }
}
