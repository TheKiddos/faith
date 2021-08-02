package org.thekiddos.faith.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.thekiddos.faith.controllers.LoginController;
import org.thekiddos.faith.dtos.UserDto;
import org.thekiddos.faith.exceptions.UserAlreadyExistException;
import org.thekiddos.faith.mappers.UserMapper;
import org.thekiddos.faith.models.PasswordResetToken;
import org.thekiddos.faith.models.User;
import org.thekiddos.faith.repositories.PasswordResetTokenRepository;
import org.thekiddos.faith.repositories.UserRepository;
import org.thekiddos.faith.utils.EmailSubjectConstants;
import org.thekiddos.faith.utils.EmailTemplatesConstants;
import org.thymeleaf.context.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class UserServiceImpl implements UserService {
    private final UserMapper userMapper = UserMapper.INSTANCE;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final PasswordResetTokenRepository passwordResetTokenRepository;

    public UserServiceImpl( UserRepository userRepository, EmailService emailService, PasswordResetTokenRepository passwordResetTokenRepository ) {
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
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
    public void rejectUser( String nickname ) throws RuntimeException {
        var user = userRepository.findByNicknameIgnoreCase( nickname ).orElse( null );
        if ( user == null ) {
            log.warn( "Attempting to delete a user that does not exists. IGNORING..." );
            return;
        }

        if ( user.isEnabled() )
            throw new RuntimeException( "Can't reject already accepted user" );

        userRepository.deleteById( user.getEmail() );
        sendAccountDeletedInfoMail( user );
    }

    private void sendAccountDeletedInfoMail( User user ) {
        Context context = new Context();
        context.setVariable( "user", user );
        emailService.sendTemplateMail( List.of( user.getEmail() ), "faith@noreplay.com", EmailSubjectConstants.ACCOUNT_DELETED, EmailTemplatesConstants.ACCOUNT_DELETED_TEMPLATE, context );
    }

    @Override
    public PasswordResetToken createForgotPasswordToken( String email ) {
        var user = userRepository.findById( email );
        if ( user.isEmpty() ) {
            log.warn( "Attempting to generate a reset token for non existing user. Ignoring..." );
            return null;
        }

        PasswordResetToken token = getTokenForUser( user.get() );
        sendPasswordResetTokenMail( token );
        return token;
    }

    @Override
    public void resetUserPassword( String token, String newPassword ) {
        var optionalToken = passwordResetTokenRepository.findByToken( token );
        if ( optionalToken.isEmpty() )
            return;

        var user = optionalToken.get().getUser();
        if ( user == null )
            return;

        user.setPassword( newPassword );
        user.setPasswordResetToken( null );
        passwordResetTokenRepository.deleteById( optionalToken.get().getId() );
        userRepository.save( user );
    }

    @Override
    public User findByEmail( String email ) {
        return (User) loadUserByUsername( email );
    }

    private PasswordResetToken getTokenForUser( User user ) {
        String tokenValue = UUID.randomUUID().toString();

        PasswordResetToken token = passwordResetTokenRepository.findByUser_Email( user.getEmail() ).orElse( new PasswordResetToken() );
        token.setUser( user );
        token.setToken( tokenValue );
        token = passwordResetTokenRepository.save( token );

        user.setPasswordResetToken( token );
        userRepository.save( user );

        return token;
    }

    private void sendPasswordResetTokenMail( PasswordResetToken token ) {
        String url = "/password-reset" + token.getToken();
        try {
            url = WebMvcLinkBuilder.linkTo( LoginController.class ).slash( "password-reset" ).slash( token.getToken() ).withSelfRel().getHref();
        }
        catch ( NullPointerException e ) {
            log.warn( "WebMvcLinkBuilder couldn't be found won't append site root to urls" );
        }
        Context context = new Context();
        context.setVariable( "url", url );

        emailService.sendTemplateMail( List.of( token.getUser().getEmail() ), "faith@noreplay.com", EmailSubjectConstants.PASSWORD_RESET, EmailTemplatesConstants.PASSWORD_RESET_TEMPLATE, context );
    }
}
