package org.thekiddos.faith.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.thekiddos.faith.validators.PasswordMatches;
import org.thekiddos.faith.validators.UniqueEmail;
import org.thekiddos.faith.validators.UniqueNickname;

import javax.validation.constraints.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@PasswordMatches
public class UserDto {
    @Email @NotNull @NotBlank
    @UniqueEmail
    private String email;
    @NotNull @NotEmpty @UniqueNickname
    @Pattern(regexp="^[A-Za-z][A-Za-z0-9]*$",message = "Invalid Input")
    private String nickname;
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
