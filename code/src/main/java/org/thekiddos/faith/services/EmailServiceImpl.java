package org.thekiddos.faith.services;

import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;

import java.util.List;

@Service
public class EmailServiceImpl implements EmailService {
    @Override
    public void sendTemplateMail( List<String> to, String from, String subject, String contents, Context context ) {
        // TODO
    }
}
