package org.thekiddos.faith.dtos;

import lombok.*;
import org.thekiddos.faith.validators.PasswordMatches;
import org.thekiddos.faith.validators.UniqueEmail;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@PasswordMatches
public class UserDTO {
    @Email @NotNull @UniqueEmail
    private String email;
    @NotNull @NotEmpty
    private String password;
    @NotNull @NotEmpty
    private String passwordConfirm;
    @Size(max = 30) @NotNull
    private String firstName;
    @Size(max = 30) @NotNull
    private String lastName;
    @NotNull
    private String phoneNumber;
    @NotNull
    private byte[] civilId;
    @NotNull
    private String address;
    private String type;
}
