package org.thekiddos.faith.models;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.MailException;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.thekiddos.faith.dtos.UserDTO;
import org.thekiddos.faith.repositories.EmailRepository;
import org.thekiddos.faith.services.EmailServiceImpl;
import org.thekiddos.faith.utils.EmailSubjectConstants;
import org.thekiddos.faith.utils.EmailTemplatesConstants;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@SpringBootTest
@ExtendWith( SpringExtension.class )
class EmailTest {
    private final EmailServiceImpl emailService;
    @MockBean
    private EmailRepository emailRepository;

    @Autowired
    public EmailTest( EmailServiceImpl emailService ) {
        this.emailService = emailService;
    }

    @Test
    void sentEmailIsSaved() {
        UserDTO user = UserDTO.builder().email( "user@test.com" ).password( "password" ).build();
        Context context = new Context();
        context.setVariable( "user", user );

        emailService.sendTemplateMail(
                List.of( "test@test.com" ),
                "no-reply@faith.com", // TODO: does this really matter?
                EmailSubjectConstants.USER_REQUIRES_APPROVAL,
                EmailTemplatesConstants.USER_REQUIRES_APPROVAL_TEMPLATE,
                context
        );
        Mockito.verify( emailRepository, Mockito.times( 1 ) ).save( any( Email.class ) );

        Email email = new Email();
        email.setFrom( "no-reply@faith.com" );
        email.setTo( "test@test.com" );
        email.setTimestamp( LocalDateTime.now() );
        email.setSubject( EmailSubjectConstants.USER_REQUIRES_APPROVAL );
        email.setTemplateName( EmailTemplatesConstants.USER_REQUIRES_APPROVAL_TEMPLATE );

        Mockito.doReturn( List.of( email ) ).when( emailRepository ).findAllByTo( "test@test.com" );
        var emails = emailService.getEmailsFor( "test@test.com" );
        assertEquals( 1, emails.size() );
        assertTrue( emails.contains( email ) );

        Mockito.doReturn( List.of( email ) ).when( emailRepository ).findAll();
        emails = emailService.getEmails();
        assertEquals( 1, emails.size() );
        assertTrue( emails.contains( email ) );
    }

    @Test
    void testEmailSendingSideEffects() {
        assertThrows( MailException.class, () -> emailService.sendTemplateMail(
                List.of( "testtest" ), // Wrong email so shouldn't save
                "no-reply@faith.com",
                EmailSubjectConstants.USER_REQUIRES_APPROVAL,
                EmailTemplatesConstants.USER_REQUIRES_APPROVAL_TEMPLATE,
                new Context()
        ) );
        Mockito.verify( emailRepository, Mockito.times( 0 ) ).save( any() );
    }
}
