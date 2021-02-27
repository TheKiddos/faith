package org.thekiddos.faith.bootstrap;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.thekiddos.faith.models.User;
import org.thekiddos.faith.repositories.UserRepository;
import org.thekiddos.faith.utils.ContextManager;


@Slf4j
@Component
public class Bootstrap implements CommandLineRunner {
    private final UserRepository userRepository;
    private final ApplicationContext context;

    @Autowired
    public Bootstrap( UserRepository userRepository, ApplicationContext context ) {
        this.userRepository = userRepository;
        this.context = context;
    }

    @Override
    public void run( String... args ) {
        ContextManager.setContext( context );

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
