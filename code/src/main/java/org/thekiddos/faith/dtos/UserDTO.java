package org.thekiddos.faith.dtos;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Data
@Builder
public class UserDTO {
    @Email @NotNull
    private String email;
}
