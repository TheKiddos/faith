package org.thekiddos.faith.models;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.thekiddos.faith.utils.Util;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

@Data
@Entity
public class User implements UserDetails {
    @Email @NotNull @Id
    private String email;

    @NotNull @NotBlank @Column( unique = true )
    @Pattern(regexp="^[A-Za-z][A-Za-z0-9]*$", message = "Invalid Input")
    private String nickname;

    @NotNull @NotEmpty
    private String password;
    @Size(max = 30) @NotNull
    private String firstName;
    @Size(max = 30) @NotNull
    private String lastName;
    private String phoneNumber;
    @Lob
    private byte[] civilId;
    private String address;
    @OneToOne( cascade = CascadeType.ALL )
    private UserType type;
    @OneToOne( cascade = CascadeType.ALL, fetch = FetchType.EAGER )
    private PasswordResetToken passwordResetToken;

    private boolean enabled = true;
    @Getter( AccessLevel.NONE )
    private boolean locked = false;
    private boolean admin = false;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String role = isAdmin() ? "ADMIN" : "USER";
        ArrayList<SimpleGrantedAuthority> auth = new ArrayList<>();
        auth.add( new SimpleGrantedAuthority( role ) );
        if ( getType() == null )
            return auth;
        auth.add( new SimpleGrantedAuthority( getType().toString().toUpperCase() ) );
        return auth;
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

    public void setNickname( String nickname ) {
        if ( nickname == null )
            return;
        this.nickname = nickname.toLowerCase();
    }

    @Override
    public String toString() {
        return "User(email=" + getEmail() + ")";
    }
}
