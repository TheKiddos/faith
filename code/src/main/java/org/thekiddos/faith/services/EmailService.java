package org.thekiddos.faith.services;

import org.thekiddos.faith.models.Email;
import org.thymeleaf.context.Context;

import java.util.List;

public interface EmailService {
    void sendTemplateMail( List<String> to, String from, String subject, String templateName, Context context );

    List<Email> getEmailsFor( String toEmail );

    List<Email> getEmails();
}
