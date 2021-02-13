package org.thekiddos.faith.bootstrap;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.thekiddos.faith.models.User;
import org.thekiddos.faith.services.UserService;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith( SpringExtension.class )
public class BootstrapTest {
    private final UserService userService;
    private final Bootstrap bootstrap;

    @Autowired
    public BootstrapTest( UserService userService, Bootstrap bootstrap ) {
        this.userService = userService;
        this.bootstrap = bootstrap;
    }

    @Test
    void defaultAdminAccountExists() {
        User user = (User) userService.loadUserByUsername( "admin@faith.com" );
        assertTrue( user.isAdmin() );
        assertTrue( user.checkPassword( "Admin@Fa1ith" ) );
        assertTrue( user.isEnabled() );
        assertTrue( user.isAccountNonLocked() );
        assertTrue( user.isCredentialsNonExpired() );
        assertTrue( user.isAccountNonExpired() );
        assertEquals( "Admin", user.getFirstName() );
        assertEquals( "Faith", user.getLastName() );
        assertEquals( Collections.singletonList( new SimpleGrantedAuthority( "ADMIN" ) ), user.getAuthorities() );
        assertNull( user.getType() );
    }

    @Test
    void onlyOneDefaultAdminCreated() {
        assertEquals( 1, userService.getAll().stream().filter( User::isAdmin ).count() );

        bootstrap.run();

        assertEquals( 1, userService.getAll().stream().filter( User::isAdmin ).count() );
    }
}
