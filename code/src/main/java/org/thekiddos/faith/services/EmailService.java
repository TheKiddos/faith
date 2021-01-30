package org.thekiddos.faith.services;

import org.thymeleaf.context.Context;

import java.util.List;

public interface EmailService {
    void sendTemplateMail( List<String> to, String from, String subject, String templateName, Context context );
}
