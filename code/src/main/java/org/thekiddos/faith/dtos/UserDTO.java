package org.thekiddos.faith.dtos;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@Builder
public class UserDTO {
    @Email @NotNull
    private String email;
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
    private UserTypeDTO type;
}
