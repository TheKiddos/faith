package org.thekiddos.faith.models;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Entity
@Data
public class PasswordResetToken {
    @Id
    @GeneratedValue( strategy = GenerationType.AUTO )
    private Long id;

    @OneToOne( mappedBy = "passwordResetToken", fetch = FetchType.EAGER )
    private User user;
    @NotNull @NotEmpty
    private String token;
    @NotNull
    private LocalDate expirationDate;

    public PasswordResetToken() {
        this.expirationDate = LocalDate.now().plusDays( 1 );
    }

    @Override
    public String toString() {
        return "Token(token=" + getToken() + ")";
    }
}
