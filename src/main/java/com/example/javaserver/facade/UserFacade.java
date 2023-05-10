package com.example.javaserver.facade;

import com.example.javaserver.dto.UserDto;
import com.example.javaserver.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserFacade {

    public UserDto userToUserDto(User user){
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setFirstname(user.getName());
        userDto.setLastname(user.getLastname());
        userDto.setUsername(user.getUsername());
        userDto.setBio(user.getBio());
        return userDto;
    }
}
