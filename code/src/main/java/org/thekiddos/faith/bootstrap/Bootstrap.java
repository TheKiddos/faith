package org.thekiddos.faith.bootstrap;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.thekiddos.faith.models.User;
import org.thekiddos.faith.repositories.UserRepository;


@Slf4j
@Component
public class Bootstrap implements CommandLineRunner {
    private final UserRepository userRepository;

    @Autowired
    public Bootstrap( UserRepository userRepository ) {
        this.userRepository = userRepository;
    }

    @Override
    public void run( String... args ) {
        if ( userRepository.findByAdminTrue().isEmpty() ) {
            createDefaultAdminAccount();
        }
    }

    private void createDefaultAdminAccount() {
        User admin = new User();
        admin.setEnabled( true );
        admin.setEmail( "admin@faith.com" );
        admin.setPassword( "Admin@Fa1ith" );
        admin.setAdmin( true );
        admin.setNickname( "Admin" );
        admin.setFirstName( "Admin" );
        admin.setLastName( "Faith" );
        userRepository.save( admin );
    }
}
