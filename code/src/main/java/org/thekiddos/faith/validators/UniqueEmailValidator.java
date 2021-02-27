package org.thekiddos.faith.validators;

import org.thekiddos.faith.repositories.UserRepository;
import org.thekiddos.faith.utils.ContextManager;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class UniqueEmailValidator implements ConstraintValidator<UniqueEmail, String> {
    private UserRepository userRepository;

    @Override
    public void initialize( UniqueEmail constraintAnnotation ) {
        userRepository = ContextManager.getBean( UserRepository.class );
    }

    @Override
    public boolean isValid( String email, ConstraintValidatorContext constraintValidatorContext ) {
        return userRepository.findById( email ).isEmpty();
    }
}
