package com.example.javaserver.web;

import com.example.javaserver.entity.User;
import com.example.javaserver.payload.JWTTokenSuccessResponse;
import com.example.javaserver.payload.request.LoginRequest;
import com.example.javaserver.payload.request.SignUpRequest;
import com.example.javaserver.payload.response.InvalidLoginResponse;
import com.example.javaserver.payload.response.MessageResponse;
import com.example.javaserver.security.SecurityConstants;
import com.example.javaserver.services.JwtService;
import com.example.javaserver.services.UserService;
import com.example.javaserver.validations.ResponseErrorValidation;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/api/auth")
@PreAuthorize("permitAll()")
@AllArgsConstructor
@Slf4j
public class AuthController {

    private ResponseErrorValidation responseErrorValidation;

    private UserService service;

    private JwtService jwtService;

    @PostMapping("/signin")
    public ResponseEntity<Object> authenticateUser(@Valid @RequestBody LoginRequest loginRequest, BindingResult bindingResult){
        ResponseEntity<Object> errors = responseErrorValidation.mapValidationService(bindingResult);
        if(!ObjectUtils.isEmpty(errors)) return errors;
        User user = service.getByEmail(loginRequest.getUsername());
        log.info(loginRequest.getPassword());
        if(!service.checkUser(user.getId(),loginRequest.getPassword())) {
            return ResponseEntity.ok(new InvalidLoginResponse());
        }
            String jwt = jwtService.createToken(user);
            Authentication authentication = jwtService.createAuthentication(jwt);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            return ResponseEntity.ok(new JWTTokenSuccessResponse(true, SecurityConstants.TOKEN_PREFIX + jwt));

        }

    @PostMapping("/signup")
    public ResponseEntity<Object> registerUser(@Valid @RequestBody SignUpRequest signUpRequest, BindingResult bindingResult){
        ResponseEntity<Object> errors = responseErrorValidation.mapValidationService(bindingResult);
        if(!ObjectUtils.isEmpty(errors)) return errors;

        service.createUser(signUpRequest);
        return ResponseEntity.ok(new MessageResponse("User register successfully"));
    }

}
