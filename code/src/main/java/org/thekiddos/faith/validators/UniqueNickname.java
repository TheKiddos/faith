package org.thekiddos.faith.validators;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;

@Constraint(validatedBy = { UniqueNicknameValidator.class })
@Target({ FIELD })
@Retention( RetentionPolicy.RUNTIME )
public @interface UniqueNickname {
    String message() default "A user with this nickname already exists";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
