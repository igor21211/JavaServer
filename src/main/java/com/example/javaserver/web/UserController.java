package com.example.javaserver.web;

import com.example.javaserver.dto.UserDto;
import com.example.javaserver.entity.User;
import com.example.javaserver.facade.UserFacade;
import com.example.javaserver.services.UserService;
import com.example.javaserver.validations.ResponseErrorValidation;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("api/user")
@CrossOrigin
@AllArgsConstructor
public class UserController {

    private UserService userService;
    private UserFacade userFacade;
    private ResponseErrorValidation responseErrorValidation;
    @GetMapping("/")
    public ResponseEntity<UserDto> getCurrentUser(Principal principal){
        User user = userService.getCurrentUser(principal);
        UserDto userDto = userFacade.userToUserDto(user);
        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }
    @GetMapping("/{userId}")
    public ResponseEntity<UserDto> getUserProfile(@PathVariable("userId") String userId){
        User user = userService.getUserById(Long.parseLong(userId));
        UserDto userDto = userFacade.userToUserDto(user);
        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }
    @PostMapping("/update")
    public ResponseEntity<Object> updateUser(@Valid @RequestBody UserDto userDto,
                                             BindingResult bindingResult,
                                             Principal principal){
        ResponseEntity<Object> errors = responseErrorValidation.mapValidationService(bindingResult);
        if(!ObjectUtils.isEmpty(errors)) return errors;

        User user = userService.updateUser(userDto,principal);
        UserDto userUpdate = userFacade.userToUserDto(user);
        return new ResponseEntity<>(userUpdate,HttpStatus.OK);
    }
}
