package org.thekiddos.faith.validators;

import org.springframework.beans.factory.annotation.Autowired;
import org.thekiddos.faith.repositories.UserRepository;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class UniqueEmailValidator implements ConstraintValidator<UniqueEmail, String> {
    @Autowired
    private UserRepository userRepository;

    @Override
    public void initialize( UniqueEmail constraintAnnotation ) {

    }

    @Override
    public boolean isValid( String email, ConstraintValidatorContext constraintValidatorContext ) {
        return userRepository.findById( email ).isEmpty();
    }
}
