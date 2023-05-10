package com.example.javaserver.services;

import com.example.javaserver.dto.PostDto;
import com.example.javaserver.entity.ImageModel;
import com.example.javaserver.entity.Post;
import com.example.javaserver.entity.User;
import com.example.javaserver.exceptions.PostNotFoundException;
import com.example.javaserver.repository.ImageRepository;
import com.example.javaserver.repository.PostRepository;
import com.example.javaserver.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@AllArgsConstructor
public class PostService {
    private PostRepository postRepository;
    private UserRepository userRepository;
    private ImageRepository imageRepository;

    public Post createPost(PostDto postDto, Principal principal){
        User user = getUserByPrincipal(principal);
        Post post = new Post();
        post.setUser(user);
        post.setCaption(postDto.getCaption());
        post.setLocation(postDto.getLocation());
        post.setTitle(postDto.getTitle());
        post.setLikes(0);
        log.info("saving post for user{}",user.getEmail());
        return postRepository.save(post);
    }

    public List<Post> getAllPost(){
        return postRepository.findAllByOrderByCreatedDateDesc();
    }

    public Post getPostById(Long postId, Principal principal){
        User user = getUserByPrincipal(principal);
        return postRepository.findPostByIdAndUser(postId,user)
                .orElseThrow(()-> new PostNotFoundException("Post cannot be found for username: "+ user.getUsername()));
    }

    public List<Post> getAllPostForUser(Principal principal){
        User user = getUserByPrincipal(principal);
        return postRepository.findAllByUserOrderByCreatedDateDesc(user);
    }

    public Post likePost(Long postId, String username){
        Post post = postRepository.findById(postId)
                .orElseThrow(()-> new PostNotFoundException("Post cannot be found"));
        Optional<String> userLiked = post.getLikedUsers()
                .stream()
                .filter(u->u.equals(username)).findAny();
        if(userLiked.isPresent()){
            post.setLikes(post.getLikes()-1);
            post.getLikedUsers().remove(username);
        }else {
            post.setLikes(post.getLikes()+1);
            post.getLikedUsers().add(username);
        }

        return postRepository.save(post);
    }

    public void deletePost(Long postId, Principal principal){
        Post post = getPostById(postId,principal);
        Optional<ImageModel> imageModel = imageRepository.findByPostId(post.getId());
        imageModel.ifPresent(imageRepository::delete);
        postRepository.deleteById(post.getId());

    }

    private User getUserByPrincipal(Principal principal){
        String username = principal.getName();
        return userRepository.findUserByEmail(username).orElseThrow(
                ()-> new UsernameNotFoundException("User name not found with username: "+username));
    }
}
