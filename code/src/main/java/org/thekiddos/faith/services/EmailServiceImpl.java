package org.thekiddos.faith.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thekiddos.faith.models.Email;
import org.thekiddos.faith.repositories.EmailRepository;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class EmailServiceImpl implements EmailService {
    private final EmailRepository emailRepository;
    private final TemplateEngine templateEngine;
    private final JavaMailSender javaMailSender;

    @Autowired
    public EmailServiceImpl( EmailRepository emailRepository, TemplateEngine templateEngine, JavaMailSender javaMailSender ) {
        this.emailRepository = emailRepository;
        this.templateEngine = templateEngine;
        this.javaMailSender = javaMailSender;
    }

    @Override
    public void sendTemplateMail( List<String> toList, String from, String subject, String template, Context context ) {
        String contents = templateEngine.process( template, context );

        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = null;

        try {
            helper = new MimeMessageHelper( message, true );

            helper.setFrom( from );
            helper.setSubject( subject );
            helper.setText( contents, true );

            for ( String to : toList ) {
                helper.setTo( to );
                javaMailSender.send( message );
                log.info( "Mail sent to: {}, with subject: {}", to, subject );

                Email email = new Email();
                email.setFrom( from );
                email.setTo( to );
                email.setSubject( subject );
                email.setTemplateName( template );
                email.setTimestamp( LocalDateTime.now() );
                emailRepository.save( email );
            }

        } catch ( MessagingException e ) {
            log.error( e.getMessage() );
        }
    }

    @Override
    public List<Email> getEmailsFor( String toEmail ) {
        return emailRepository.findAllByTo( toEmail );
    }

    @Override
    public List<Email> getEmails() {
        return emailRepository.findAll();
    }
}
