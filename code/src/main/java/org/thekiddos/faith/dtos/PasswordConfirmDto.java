package org.thekiddos.faith.dtos;

import lombok.Data;
import org.thekiddos.faith.validators.PasswordMatches;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@PasswordMatches
public class PasswordConfirmDto {
    @NotNull @NotBlank
    private String password;
    @NotNull @NotBlank
    private String passwordConfirm;
}
