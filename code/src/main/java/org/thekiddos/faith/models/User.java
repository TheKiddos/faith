package org.thekiddos.faith.models;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.thekiddos.faith.utils.Util;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToOne;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Collection;
import java.util.Objects;

@Data
@Entity
public class User implements UserDetails {
    @Email @NotNull @Id
    private String email;

    @NotNull @NotEmpty
    private String password;
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

    private boolean enabled = true;
    @Getter( AccessLevel.NONE )
    private boolean locked = false;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !locked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
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

    public void setPassword( String password ) {
        this.password = Util.PASSWORD_ENCODER.encode( password );
    }

    public boolean checkPassword( String password ) {
        return Util.PASSWORD_ENCODER.matches( password, getPassword() );
    }

    // TODO: implement real one
    public boolean isAdmin() {
        return true;
    }
}
