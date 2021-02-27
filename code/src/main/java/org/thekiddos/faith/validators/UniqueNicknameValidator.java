package org.thekiddos.faith.validators;

import org.thekiddos.faith.repositories.UserRepository;
import org.thekiddos.faith.utils.ContextManager;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class UniqueNicknameValidator implements ConstraintValidator<UniqueNickname, String> {
    private UserRepository userRepository;

    @Override
    public void initialize( UniqueNickname constraintAnnotation ) {
        userRepository = ContextManager.getBean( UserRepository.class );
    }

    @Override
    public boolean isValid( String nickname, ConstraintValidatorContext constraintValidatorContext ) {
        return userRepository.findByNicknameIgnoreCase( nickname ).isEmpty();
    }
}
