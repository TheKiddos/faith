package org.thekiddos.faith.models;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToOne;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Collection;
import java.util.Objects;

@Data
@Entity
public class User implements UserDetails {
    @Email @NotNull @Id
    private String email;

    @Size(max = 30) @NotNull
    private String firstName;
    @Size(max = 30) @NotNull
    private String lastName;
    @NotNull
    private String phoneNumber;
    @NotNull @Lob
    private byte[] civilId;
    @NotNull
    private String address;
    @OneToOne
    private UserType type;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return null;
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) return true;
        if ( o == null || getClass() != o.getClass() ) return false;
        User user = (User) o;
        return email.equals( user.email );
    }

    @Override
    public int hashCode() {
        return Objects.hash( email );
    }
}
