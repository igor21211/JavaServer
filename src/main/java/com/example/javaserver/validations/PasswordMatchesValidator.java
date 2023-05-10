package com.example.javaserver.validations;

import com.example.javaserver.annotations.PasswordMatches;
import com.example.javaserver.payload.request.SignUpRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, Object> {


    @Override
    public void initialize(PasswordMatches constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(Object object, ConstraintValidatorContext constraintValidatorContext) {
        SignUpRequest signUpRequest = (SignUpRequest) object;
        return signUpRequest.getPassword().equals(signUpRequest.getConfirmPassword());

    }
}
