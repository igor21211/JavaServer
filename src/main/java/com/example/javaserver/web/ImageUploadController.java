package com.example.javaserver.web;

import com.example.javaserver.entity.ImageModel;
import com.example.javaserver.payload.response.MessageResponse;
import com.example.javaserver.services.ImageUploadService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;

@RestController
@RequestMapping("api/image")
@CrossOrigin
@AllArgsConstructor
public class ImageUploadController {

    private ImageUploadService imageUploadService;

    @PostMapping("/upload")
    public ResponseEntity<MessageResponse> uploadImageToUser(@RequestParam("file")MultipartFile file,
                                                             Principal principal) throws IOException {
        imageUploadService.uploadImageToUser(file,principal);
        return ResponseEntity.ok(new MessageResponse("Image Upload Successfully"));
    }

    @PostMapping("/{postId}/upload")
    public ResponseEntity<MessageResponse> uploadImageToPost(@PathVariable("postId") String postId,
                                                             @RequestParam("file") MultipartFile file,
                                                             Principal principal)throws IOException{
        imageUploadService.uploadImageToPost(file,principal,Long.parseLong(postId));
        return ResponseEntity.ok(new MessageResponse("Image Upload Successfully"));
    }
    @GetMapping("/profileImage")
    public ResponseEntity<ImageModel> getImageForUser(Principal principal){
        ImageModel userImage = imageUploadService.getImageToUser(principal);
        return new ResponseEntity<>(userImage, HttpStatus.OK);
    }

    @GetMapping("/{postId}/image")
    public ResponseEntity<ImageModel> getImageToPost(@PathVariable("postId") String postId){
        ImageModel postImage = imageUploadService.getImageToPost(Long.parseLong(postId));
        return new ResponseEntity<>(postImage,HttpStatus.OK);
    }
}
