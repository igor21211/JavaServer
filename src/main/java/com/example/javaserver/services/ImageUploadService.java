package com.example.javaserver.services;

import com.example.javaserver.entity.ImageModel;
import com.example.javaserver.entity.Post;
import com.example.javaserver.entity.User;
import com.example.javaserver.exceptions.ImageNotFoundException;
import com.example.javaserver.exceptions.PostNotFoundException;
import com.example.javaserver.repository.ImageRepository;
import com.example.javaserver.repository.PostRepository;
import com.example.javaserver.repository.UserRepository;
import io.netty.util.internal.ObjectUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.xml.crypto.Data;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.Principal;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

@Service
@AllArgsConstructor
@Slf4j
public class ImageUploadService {

    private ImageRepository imageRepository;
    private UserRepository userRepository;
    private PostRepository postRepository;


    public ImageModel uploadImageToUser(MultipartFile multipartFile, Principal principal) throws IOException{
        User user = getUserByPrincipal(principal);
        log.info("Uploading image profile to user {}: ", user.getUsername());
        ImageModel userProfileImage = imageRepository.findByUserId(user.getId()).orElse(null);
        if(!ObjectUtils.isEmpty(userProfileImage)){
            imageRepository.delete(userProfileImage);
        }

        ImageModel imageModel = new ImageModel();
        imageModel.setUserId(user.getId());
        imageModel.setImageByte(compressBytes(multipartFile.getBytes()));
        imageModel.setName(multipartFile.getOriginalFilename());
        return imageRepository.save(imageModel);
    }

    public ImageModel uploadImageToPost(MultipartFile file , Principal principal, Long postId) throws IOException{
        User user = getUserByPrincipal(principal);
        Post post = user.getPosts()
                .stream()
                .filter(p->p.getId().equals(postId))
                .findFirst().orElseThrow(() -> new PostNotFoundException("Post wasn't found. post id = " + postId));
        ImageModel imageModel = new ImageModel();
        imageModel.setPostId(post.getId());
        imageModel.setImageByte(compressBytes(file.getBytes()));
        imageModel.setName(file.getOriginalFilename());
        log.info("Upload Image to Post {}: "+ post.getId());
        return imageRepository.save(imageModel);
    }

    public ImageModel getImageToUser(Principal principal){
        User user = getUserByPrincipal(principal);
        ImageModel imageModel = imageRepository.findByUserId(user.getId()).orElse(null);
        if(!ObjectUtils.isEmpty(imageModel)){
            imageModel.setImageByte(decompressBytes(imageModel.getImageByte()));
        }
        return imageModel;
    }

    public ImageModel getImageToPost(Long postId){
        ImageModel imageModel = imageRepository.findByPostId(postId)
                .orElseThrow(()-> new ImageNotFoundException("Cannot find image to Post: "+postId));
        if(!ObjectUtils.isEmpty(imageModel)){
            imageModel.setImageByte(decompressBytes(imageModel.getImageByte()));
        }
        return imageModel;
    }
    private byte[] compressBytes(byte[] data){
        Deflater deflater = new Deflater();
        deflater.setInput(data);
        deflater.finish();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(data.length);
        byte [] buffer = new byte[1024];
        while (!deflater.finished()){
            int count = deflater.deflate(buffer);
            byteArrayOutputStream.write(buffer,0,count);
        }
        try{
            byteArrayOutputStream.close();
        }catch (IOException e){
            log.error("Cannot compress Bytes");
        }
        System.out.println("Compressed Image Byte Size - " + byteArrayOutputStream.toByteArray().length);
        return byteArrayOutputStream.toByteArray();
    }

    private static byte[] decompressBytes(byte[] data){
        Inflater inflater = new Inflater();
        inflater.setInput(data);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
        byte [] buffer = new byte[1024];
        try{
            while (!inflater.finished()){
                int count = inflater.inflate(buffer);
                outputStream.write(buffer,0,count);
            }
            outputStream.close();
        }catch (IOException | DataFormatException e){
            log.error("Cannot decompress Bytes");
        }
        return outputStream.toByteArray();
    }
    
    private User getUserByPrincipal(Principal principal){
        String username = principal.getName();
        return userRepository.findUserByEmail(username).orElseThrow(
                ()-> new UsernameNotFoundException("User name not found with username: "+username));
    }
}
