package com.example.javaserver.web;

import com.example.javaserver.dto.PostDto;
import com.example.javaserver.entity.Post;
import com.example.javaserver.facade.PostFacade;
import com.example.javaserver.payload.response.MessageResponse;
import com.example.javaserver.services.PostService;
import com.example.javaserver.validations.ResponseErrorValidation;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.message.Message;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/post")
@CrossOrigin
@AllArgsConstructor
public class PostController {

    private PostFacade postFacade;
    private PostService postService;
    private ResponseErrorValidation responseErrorValidation;

    @PostMapping("/create")
    public ResponseEntity<Object> createPost(@Valid @RequestBody PostDto postDto,
                                             BindingResult bindingResult,
                                             Principal principal ){
        ResponseEntity<Object> errors = responseErrorValidation.mapValidationService(bindingResult);
        if(!ObjectUtils.isEmpty(errors)) return errors;
        Post post = postService.createPost(postDto, principal);
        PostDto createPost = postFacade.postToPostDto(post);
        return new ResponseEntity<>(createPost, HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<List<PostDto>> getAllPost(){
        List<PostDto> postDtos = postService.getAllPost()
                .stream()
                .map(postFacade::postToPostDto)
                .collect(Collectors.toList());
        return new ResponseEntity<>(postDtos,HttpStatus.OK);
    }
    @GetMapping("/user/posts")
    public ResponseEntity<List<PostDto>> getAllPostsForUser(Principal principal){
        List<PostDto> postDtos = postService.getAllPostForUser(principal)
                .stream()
                .map(postFacade::postToPostDto)
                .collect(Collectors.toList());
        return new ResponseEntity<>(postDtos, HttpStatus.OK);
    }

    @PostMapping("/{postId}/{username}/like")
    public ResponseEntity<PostDto> likePost(@PathVariable("postId") String postId,
                                            @PathVariable("username") String username){
        Post post = postService.likePost(Long.parseLong(postId),username);
        PostDto postDto = postFacade.postToPostDto(post);
        return new ResponseEntity<>(postDto,HttpStatus.OK);
    }
    @PostMapping("/{postId}/delete")
    public ResponseEntity<MessageResponse> deletePost(@PathVariable("postId") String postId, Principal principal){
        postService.deletePost(Long.parseLong(postId),principal);
        return new ResponseEntity<>(new MessageResponse("Post was deleted"), HttpStatus.OK);
    }

}
