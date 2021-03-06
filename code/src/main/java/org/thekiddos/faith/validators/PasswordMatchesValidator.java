package org.thekiddos.faith.validators;

import org.thekiddos.faith.dtos.PasswordConfirmDto;
import org.thekiddos.faith.dtos.UserDto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, Object> {

    @Override
    public void initialize( PasswordMatches constraintAnnotation ) {
    }

    @Override
    public boolean isValid( Object obj, ConstraintValidatorContext context ) {
        // TODO: use polymorphism
        try {
            UserDto user = (UserDto) obj;
            return user.getPassword().equals( user.getPasswordConfirm() );
        }
        catch ( ClassCastException e ) {
            // Assume a user is resetting his password
            PasswordConfirmDto passwordConfirmDto = (PasswordConfirmDto) obj;
            return passwordConfirmDto.getPassword().equals( passwordConfirmDto.getPasswordConfirm() );
        }
    }
}
