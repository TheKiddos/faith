package org.thekiddos.faith.models;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.thekiddos.faith.dtos.UserDto;
import org.thekiddos.faith.exceptions.UserAlreadyExistException;
import org.thekiddos.faith.mappers.UserMapper;
import org.thekiddos.faith.repositories.PasswordResetTokenRepository;
import org.thekiddos.faith.repositories.UserRepository;
import org.thekiddos.faith.services.EmailService;
import org.thekiddos.faith.services.UserServiceImpl;
import org.thekiddos.faith.utils.EmailSubjectConstants;
import org.thekiddos.faith.utils.EmailTemplatesConstants;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.*;

@ExtendWith( MockitoExtension.class )
class UserTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private EmailService emailService;
    @Mock
    private PasswordResetTokenRepository passwordResetTokenRepository;
    @InjectMocks
    private UserServiceImpl userService;
    private final UserMapper userMapper = UserMapper.INSTANCE;

    @Test
    void createUser() {
        UserDto userDTO = UserDto.builder().email( "test@test.com" ).password( "password" ).build();
        User user = userMapper.userDtoToUser( userDTO );

        Mockito.doReturn( Optional.empty() ).when( userRepository ).findById( anyString() );

        assertThrows( UsernameNotFoundException.class, () -> userService.loadUserByUsername( "test@test.com" ) );

        Mockito.doReturn( user ).when( userRepository ).save( any( User.class ) );

        user = userService.createUser( userDTO );

        Mockito.doReturn( List.of( user ) ).when( userRepository ).findAll();

        assertEquals( 1, userService.getAll().size() );
        assertTrue( userService.getAll().contains( user ) );

        assertFalse( user.isAdmin() );
        assertEquals( Collections.singletonList( new SimpleGrantedAuthority( "USER" ) ), user.getAuthorities() );
    }

    @Test
    void userAccountManagement() {
        UserDto userDTO = UserDto.builder().email( "test@test.com" ).password( "password" ).build();
        User user = userMapper.userDtoToUser( userDTO );

        assertEquals( user.getEmail(), user.getUsername() );

        assertTrue( user.isEnabled() );
        user.setEnabled( false );
        assertFalse( user.isEnabled() );

        assertTrue( user.isAccountNonExpired() );
        assertTrue( user.isCredentialsNonExpired() );
        assertTrue( user.isAccountNonLocked() );
        assertFalse( user.isAdmin() );
        assertEquals( Collections.singletonList( new SimpleGrantedAuthority( "USER" ) ), user.getAuthorities() );
    }

    @Test
    void requireAdminApprovalFor() {
        UserDto userDTO = UserDto.builder().email( "test@test.com" ).password( "password" ).build();
        User user = userMapper.userDtoToUser( userDTO );
        assertTrue( user.isEnabled() );

        UserDto adminDTO = UserDto.builder().email( "admin@test.com" ).password( "password" ).build();
        User admin = userMapper.userDtoToUser( adminDTO );

        Mockito.doReturn( List.of( admin ) ).when( userRepository ).findByAdminTrue();
        Mockito.doReturn( null ).when( userRepository ).save( any() );
        userService.requireAdminApprovalFor( user );
        assertFalse( user.isEnabled() );
        Mockito.verify( emailService, Mockito.times( 1 ) )
                .sendTemplateMail( eq( List.of( admin.getEmail() ) ), anyString(), anyString(), anyString(), any() );
    }

    @Test
    void uniqueEmail() {
        UserDto userDTO = UserDto.builder().email( "test@test.com" ).password( "password" ).build();
        User user = userMapper.userDtoToUser( userDTO );

        Mockito.doReturn( Optional.empty() ).when( userRepository ).findById( anyString() );
        Mockito.doReturn( user ).when( userRepository ).save( any( User.class ) );

        user = userService.createUser( userDTO );

        Mockito.doReturn(  Optional.of( user ) ).when( userRepository ).findById( anyString() );

        assertThrows( UserAlreadyExistException.class , () -> userService.createUser( userDTO ) );

        Mockito.doReturn( List.of( user ) ).when( userRepository ).findAll();
        assertEquals( 1, userService.getAll().size() );
        assertTrue( userService.getAll().contains( user ) );
    }

    @Test
    void equalsAndHashcode() {
        User user = new User();
        user.setEmail( "abc@gmail.com" );

        User otherUser = new User();
        otherUser.setEmail( "abc@gmail2.com" );

        assertNotEquals( user, otherUser );
        assertEquals( user, user );
        assertNotEquals( user, null );
        assertNotEquals( user.hashCode(), otherUser.hashCode() );

        UserDto userDto = new UserDto();
        userDto.setEmail( "abc@gmail.com" );

        UserDto otherUserDto = new UserDto();
        otherUserDto.setEmail( "abc@gmail2.com" );

        assertNotEquals( userDto, otherUserDto );
        assertEquals( userDto, userDto );
        assertNotEquals( userDto, null );
        assertNotEquals( userDto.hashCode(), otherUserDto.hashCode() );
    }

    @Test
    void activateUser() {
        String nickname = "someidiot";
        UserDto userDTO = UserDto.builder().email( "test@test.com" ).password( "password" ).nickname( nickname ).build();
        User user = userMapper.userDtoToUser( userDTO );
        user.setEnabled( false );

        Mockito.doReturn( Optional.of( user ) ).when( userRepository ).findByNicknameIgnoreCase( nickname );

        userService.activateUser( nickname );

        assertTrue( user.isEnabled() );
        Mockito.verify( emailService, Mockito.times( 1 ) )
                .sendTemplateMail( eq( List.of( user.getEmail() ) ), anyString(), eq( EmailSubjectConstants.ACCOUNT_ACTIVATED ), eq( EmailTemplatesConstants.ACCOUNT_ACTIVATED_TEMPLATE ), any() );
    }

    @Test
    void activateUserThatIsAlreadyActivatedDoesNothing() {
        String nickname = "someidiot";
        UserDto userDTO = UserDto.builder().email( "test@test.com" ).password( "password" ).nickname( nickname ).build();
        User user = userMapper.userDtoToUser( userDTO );
        user.setEnabled( true );

        Mockito.doReturn( Optional.of( user ) ).when( userRepository ).findByNicknameIgnoreCase( nickname );

        userService.activateUser( nickname );

        assertTrue( user.isEnabled() );
    }

    @Test
    void activateUserThatDoesNotExistsDoesNothing() {
        String nickname = "someidiot";

        Mockito.doReturn( Optional.empty() ).when( userRepository ).findByNicknameIgnoreCase( nickname );

        userService.activateUser( nickname );

        // No exception was thrown so we are done
    }

    @Test
    void normalizedNickname() {
        UserDto userDTO = UserDto.builder().email( "test@test.com" ).password( "password" ).nickname( "NiCkname" ).build();
        User user = userMapper.userDtoToUser( userDTO );
        assertEquals( "nickname", user.getNickname() );
    }

    @Test
    void nullNickname() {
        UserDto userDTO = UserDto.builder().email( "test@test.com" ).password( "password" ).nickname( null ).build();
        userMapper.userDtoToUser( userDTO );

        // Nothing thrown we are good
    }

    @Test
    void deleteUser() {
        String nickname = "someidiot";
        UserDto userDTO = UserDto.builder().email( "test@test.com" ).password( "password" ).nickname( nickname ).build();
        User user = userMapper.userDtoToUser( userDTO );

        Mockito.doReturn( Optional.of( user ) ).when( userRepository ).findByNicknameIgnoreCase( nickname );

        userService.deleteUser( nickname );

        Mockito.verify( userRepository, Mockito.times( 1 ) ).deleteById( user.getEmail() );
        Mockito.verify( emailService, Mockito.times( 1 ) )
                .sendTemplateMail( eq( List.of( user.getEmail() ) ), anyString(), eq( EmailSubjectConstants.ACCOUNT_DELETED ), eq( EmailTemplatesConstants.ACCOUNT_DELETED_TEMPLATE ), any() );
    }

    @Test
    void deleteUserThatDoesNotExistsDoesNothing() {
        String nickname = "someidiot";

        Mockito.doReturn( Optional.empty() ).when( userRepository ).findByNicknameIgnoreCase( nickname );

        userService.deleteUser( nickname );

        // No exception was thrown so we are done
    }

    @Test
    void createForgotPasswordToken() {
        UserDto userDTO = UserDto.builder().email( "test@test.com" ).password( "password" ).build();
        User user = userMapper.userDtoToUser( userDTO );

        Mockito.doReturn( Optional.of( user ) ).when( userRepository ).findById( user.getEmail() );
        Mockito.doReturn( Optional.empty() ).when( passwordResetTokenRepository ).findByUser_Email( user.getEmail() );
        Mockito.doAnswer( returnsFirstArg() ).when( passwordResetTokenRepository ).save( any( PasswordResetToken.class ) );
        PasswordResetToken token = userService.createForgotPasswordToken( user.getEmail() );

        assertEquals( user.getEmail(), token.getUser().getEmail() );
        assertEquals( 1, Duration.between( LocalDateTime.now(), LocalDateTime.now().with( token.getExpirationDate() ) ).toDays() );

        Mockito.verify( passwordResetTokenRepository, Mockito.times( 1 ) ).save( any( PasswordResetToken.class ) );
        Mockito.verify( emailService, Mockito.times( 1 ) )
                .sendTemplateMail( eq( List.of( user.getEmail() ) ), anyString(), eq( EmailSubjectConstants.PASSWORD_RESET ), eq( EmailTemplatesConstants.PASSWORD_RESET_TEMPLATE ), any() );
        Mockito.verify( userRepository, Mockito.times( 1 ) ).save( any() );

    }

    @Test
    void createForgotPasswordTokenAndReplaceOldToken() {
        UserDto userDTO = UserDto.builder().email( "test@test.com" ).password( "password" ).build();
        User user = userMapper.userDtoToUser( userDTO );

        Mockito.doReturn( Optional.of( user ) ).when( userRepository ).findById( user.getEmail() );
        Mockito.doReturn( Optional.of( new PasswordResetToken() ) ).when( passwordResetTokenRepository ).findByUser_Email( user.getEmail() );
        Mockito.doAnswer( returnsFirstArg() ).when( passwordResetTokenRepository ).save( any( PasswordResetToken.class ) );
        PasswordResetToken token = userService.createForgotPasswordToken( user.getEmail() );

        assertEquals( user.getEmail(), token.getUser().getEmail() );
        assertEquals( 1, Duration.between( LocalDateTime.now(), LocalDateTime.now().with( token.getExpirationDate() ) ).toDays() );

        Mockito.verify( passwordResetTokenRepository, Mockito.times( 1 ) ).save( any( PasswordResetToken.class ) );
        Mockito.verify( emailService, Mockito.times( 1 ) )
                .sendTemplateMail( eq( List.of( user.getEmail() ) ), anyString(), eq( EmailSubjectConstants.PASSWORD_RESET ), eq( EmailTemplatesConstants.PASSWORD_RESET_TEMPLATE ), any() );
        Mockito.verify( userRepository, Mockito.times( 1 ) ).save( any() );
    }

    @Test
    void resetUserPassword() {
        var token = new PasswordResetToken();
        var user = new User();
        token.setUser( user );

        Mockito.doReturn( Optional.of( token ) ).when( passwordResetTokenRepository ).findByToken( "token" );
        userService.resetUserPassword( "token", "newpassword" );

        assertTrue( user.checkPassword( "newpassword" ) );
        Mockito.verify( userRepository, Mockito.times( 1 ) ).save( user );
        Mockito.verify( passwordResetTokenRepository, Mockito.times( 1 ) ).deleteById( any() );
    }

    @Test
    void resetUserPasswordWithNoToken() {
        Mockito.doReturn( Optional.empty() ).when( passwordResetTokenRepository ).findByToken( any() );
        userService.resetUserPassword( "token", "newpassword" );

        Mockito.verify( userRepository, Mockito.times( 0 ) ).save( any() );
        Mockito.verify( passwordResetTokenRepository, Mockito.times( 0 ) ).deleteById( any() );
    }

    @Test
    void resetUserPasswordWithInvalidToken() {
        var token = new PasswordResetToken();

        Mockito.doReturn( Optional.of( token ) ).when( passwordResetTokenRepository ).findByToken( "token" );
        userService.resetUserPassword( "token", "newpassword" );

        Mockito.verify( userRepository, Mockito.times( 0 ) ).save( any() );
        Mockito.verify( passwordResetTokenRepository, Mockito.times( 0 ) ).deleteById( any() );
    }
}
