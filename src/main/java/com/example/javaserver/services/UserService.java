package com.example.javaserver.services;

import com.example.javaserver.dto.UserDto;
import com.example.javaserver.entity.User;
import com.example.javaserver.exceptions.UserExistException;
import com.example.javaserver.payload.request.SignUpRequest;
import com.example.javaserver.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.Optional;

@Service
@Slf4j
@AllArgsConstructor
public class UserService {
    private final UserRepository repository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public User createUser(SignUpRequest signUpRequest) {
        User user  = new User();
       user.setEmail(signUpRequest.getEmail());
       user.setName(signUpRequest.getFirstname());
       user.setLastname(signUpRequest.getLastname());
       user.setUsername(signUpRequest.getUsername());
       user.setRole("User");
       user.setPassword(bCryptPasswordEncoder.encode(signUpRequest.getPassword()));
       try{
           log.info("Saving user{}", signUpRequest.getEmail());
           return repository.save(user);
       }catch (Exception e){
            log.error("Error during registration. {}", e.getMessage());
            throw new UserExistException("The User "+ user.getUsername()+" already exist. Please check credentials");
       }
    }

    public User getByEmail(String email){
        return repository.findUserByEmail(email).orElseThrow(()-> new UsernameNotFoundException("User not found with email: "+ email));
    }

    public boolean checkUser(Long id,String password){
      User user =  repository.findUserById(id).orElse(null);
      if(bCryptPasswordEncoder.matches(password, user.getPassword())){
          return true;
      }
      return false;
    }

    public User updateUser(UserDto userDto, Principal principal){
        User user = getUserByPrincipal(principal);
        user.setName(userDto.getFirstname());
        user.setLastname(userDto.getLastname());
        user.setBio(userDto.getBio());
        return repository.save(user);
    }

    public User getCurrentUser(Principal principal){
        return getUserByPrincipal(principal);
    }

    private User getUserByPrincipal(Principal principal){
        String username = principal.getName();
        return repository.findUserByEmail(username).orElseThrow(
                ()-> new UsernameNotFoundException("User name not found with username: "+username));
    }

    public User getUserById(long userId) {
        return repository.findUserById(userId)
                .orElseThrow(()-> new UsernameNotFoundException("User not found with id: " + userId));
    }
}
