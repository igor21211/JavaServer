package com.example.javaserver.services;

import com.example.javaserver.dto.CommentDto;
import com.example.javaserver.entity.Comment;
import com.example.javaserver.entity.Post;
import com.example.javaserver.entity.User;
import com.example.javaserver.exceptions.PostNotFoundException;
import com.example.javaserver.repository.CommentRepository;
import com.example.javaserver.repository.PostRepository;
import com.example.javaserver.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public Comment saveComment(Long postId, CommentDto commentDto, Principal principal){
        User user = getUserByPrincipal(principal);
        Post post = postRepository.findById(postId)
                .orElseThrow(()-> new PostNotFoundException("Post cannot be found for username: " +user.getEmail()));
        Comment comment = new Comment();
        comment.setPost(post);
        comment.setUserId(user.getId());
        comment.setUsername(user.getUsername());
        comment.setMessage(commentDto.getMessage());
        log.info("Saving comment for Post: {}", post.getId());
        return commentRepository.save(comment);
    }

    public void deleteComment(Long commentId){
        Optional<Comment> comment = commentRepository.findById(commentId);
        comment.ifPresent(commentRepository::delete);
    }
    public List<Comment> getAllCommentsForPost(Long postId){
        Post post = postRepository.findById(postId)
                .orElseThrow(()-> new PostNotFoundException("Post cannot be found"));
        List<Comment> comments = commentRepository.findAllByPost(post);
        return comments;
    }

    private User getUserByPrincipal(Principal principal){
        String username = principal.getName();
        return userRepository.findUserByEmail(username).orElseThrow(
                ()-> new UsernameNotFoundException("User name not found with username: "+username));
    }
}
