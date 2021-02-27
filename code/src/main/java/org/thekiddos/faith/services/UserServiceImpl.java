package org.thekiddos.faith.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.thekiddos.faith.dtos.UserDto;
import org.thekiddos.faith.exceptions.UserAlreadyExistException;
import org.thekiddos.faith.mappers.UserMapper;
import org.thekiddos.faith.models.User;
import org.thekiddos.faith.repositories.UserRepository;
import org.thekiddos.faith.utils.EmailSubjectConstants;
import org.thekiddos.faith.utils.EmailTemplatesConstants;
import org.thymeleaf.context.Context;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class UserServiceImpl implements UserService {
    private final UserMapper userMapper = UserMapper.INSTANCE;
    private final UserRepository userRepository;
    private final EmailService emailService;

    public UserServiceImpl( UserRepository userRepository, EmailService emailService ) {
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

    @Override
    public User createUser( UserDto userDto ) throws UserAlreadyExistException {
        if ( userRepository.findById( userDto.getEmail() ).isPresent() )
            throw new UserAlreadyExistException( "A user with this email already exists" );

        User user = userMapper.userDtoToUser( userDto );
        return userRepository.save( user );
    }

    @Override
    public List<User> getAll() {
        return userRepository.findAll();
    }

    @Override
    public void requireAdminApprovalFor( User user ) {
        user.setEnabled( false );
        userRepository.save( user );
        Context context = new Context();
        context.setVariable( "user", user );
        emailService.sendTemplateMail( getAdminEmails(), "faith@noreplay.com", EmailSubjectConstants.USER_REQUIRES_APPROVAL, EmailTemplatesConstants.USER_REQUIRES_APPROVAL_TEMPLATE, context );
    }

    private List<String> getAdminEmails() {
        List<String> adminEmails = new ArrayList<>();
        userRepository.findByAdminTrue().forEach( user -> adminEmails.add( user.getEmail() ) );
        return adminEmails;
    }

    @Override
    public UserDetails loadUserByUsername( String email ) throws UsernameNotFoundException {
        return userRepository.findById( email ).orElseThrow( () -> new UsernameNotFoundException( "No user with specified email was found" ) );
    }

    @Override
    public void activateUser( String nickname ) {
        User user = userRepository.findByNicknameIgnoreCase( nickname ).orElse( null );

        if ( user == null ) {
            log.warn( "Attempting to activate a user that does not exists. IGNORING...");
            return;
        }

        user.setEnabled( true );
        userRepository.save( user );

        sendActivationInfoMail( user );
    }

    private void sendActivationInfoMail( User user ) {
        Context context = new Context();
        context.setVariable( "user", user );
        emailService.sendTemplateMail( List.of( user.getEmail() ), "faith@noreplay.com", EmailSubjectConstants.ACCOUNT_ACTIVATED, EmailTemplatesConstants.ACCOUNT_ACTIVATED_TEMPLATE, context );
    }

    @Override
    public void deleteUser( String nickname ) {
        var user = userRepository.findByNicknameIgnoreCase( nickname );
        if ( user.isEmpty() ) {
            log.warn( "Attempting to delete a user that does not exists. IGNORING..." );
            return;
        }

        userRepository.deleteById( user.get().getEmail() );
        sendAccountDeletedInfoMail( user.get() );
    }

    private void sendAccountDeletedInfoMail( User user ) {
        Context context = new Context();
        context.setVariable( "user", user );
        emailService.sendTemplateMail( List.of( user.getEmail() ), "faith@noreplay.com", EmailSubjectConstants.ACCOUNT_DELETED, EmailTemplatesConstants.ACCOUNT_DELETED_TEMPLATE, context );
    }
}
