package com.wellnr.common.helper;

import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessagePreparator;

import java.io.InputStream;
import java.text.MessageFormat;
import java.util.Objects;

public class FakeMailSender implements JavaMailSender {
    @Override
    public MimeMessage createMimeMessage() {
        return null;
    }

    @Override
    public MimeMessage createMimeMessage(InputStream contentStream) throws MailException {
        return null;
    }

    @Override
    public void send(MimeMessage mimeMessage) throws MailException {

    }

    @Override
    public void send(MimeMessage... mimeMessages) throws MailException {

    }

    @Override
    public void send(MimeMessagePreparator mimeMessagePreparator) throws MailException {

    }

    @Override
    public void send(MimeMessagePreparator... mimeMessagePreparators) throws MailException {

    }

    @Override
    public void send(SimpleMailMessage simpleMessage) throws MailException {
        System.out.println(MessageFormat.format("""
                SENDING MAIL
                ------------
                            
                From: {0}
                To:   {1}
                            
                {2}
                            
                ---
                """,
            simpleMessage.getFrom(),
            String.join(", ", Objects.requireNonNull(simpleMessage.getTo())),
            simpleMessage.getText()
        ));
    }

    @Override
    public void send(SimpleMailMessage... simpleMessages) throws MailException {
        for (var message : simpleMessages) {
            this.send(message);
        }
    }

}
