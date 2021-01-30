package org.thekiddos.faith.validators;

import org.thekiddos.faith.dtos.UserDTO;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, Object> {

    @Override
    public void initialize(PasswordMatches constraintAnnotation) {
    }

    @Override
    public boolean isValid(Object obj, ConstraintValidatorContext context){
        UserDTO user = (UserDTO) obj;
        return user.getPassword().equals( user.getPasswordConfirm() );
    }
}
