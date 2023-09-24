package com.wellnr.common.helper;

import com.wellnr.common.Operators;
import com.wellnr.schooltrip.core.application.SchoolTripApplicationConfiguration;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.*;

import java.io.InputStream;
import java.text.MessageFormat;
import java.util.Objects;

@AllArgsConstructor
public class FakeMailSender implements JavaMailSender {

    private SchoolTripApplicationConfiguration config;

    @Override
    public MimeMessage createMimeMessage() {
        var cfg = config.getEmail();
        var mailSender = new JavaMailSenderImpl();
        mailSender.setHost(cfg.getHost());
        mailSender.setPort(cfg.getPort());

        mailSender.setUsername(cfg.getUsername());
        mailSender.setPassword(cfg.getPassword());

        var props = mailSender.getJavaMailProperties();
        props.putAll(cfg.getProperties());

        return mailSender.createMimeMessage();
    }

    @Override
    public MimeMessage createMimeMessage(InputStream contentStream) throws MailException {
        return null;
    }

    @Override
    public void send(MimeMessage mimeMessage) throws MailException {
        System.out.println(MessageFormat.format("""
                SENDING MAIL
                ------------
                            
                From: {0}
                To:   {1}
                            
                {2}
                            
                ---
                """,
            Operators.suppressExceptions(mimeMessage::getFrom),
            Operators.suppressExceptions(mimeMessage::getAllRecipients),
            Operators.suppressExceptions(mimeMessage::getContent)
        ));
    }

    @Override
    public void send(MimeMessage... mimeMessages) throws MailException {
        for (var message : mimeMessages) {
            this.send(message);
        }
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
