package org.thekiddos.faith.validators;

import org.springframework.beans.factory.annotation.Autowired;
import org.thekiddos.faith.repositories.UserRepository;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class UniqueNicknameValidator implements ConstraintValidator<UniqueNickname, String> {
    @Autowired
    private UserRepository userRepository;

    @Override
    public boolean isValid( String nickname, ConstraintValidatorContext constraintValidatorContext ) {
        return userRepository.findByNickname( nickname ).isEmpty();
    }
}
