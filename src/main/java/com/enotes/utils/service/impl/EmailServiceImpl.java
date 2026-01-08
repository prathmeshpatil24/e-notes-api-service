package com.enotes.utils.service.impl;

import com.enotes.utils.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService  {

    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${mail.from}")
    private String mailFrom;//dynamically get from app.property

    //MimeMessage: Used for HTML content or attachments
    @Override
    public void mimeEmailForm(String toEmail, String subject, String body) throws MessagingException {

            MimeMessage mailMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mailMessage,true);

            helper.setTo(toEmail);
            helper.setSubject(subject);
            //it is hard coded.
            //helper.setText("<h1>Hello</h1><p>This is an <b>HTML</b> email!</p>", true);

            helper.setText(body, true); // Use the passed `body` variable (with HTML=true)

            javaMailSender.send(mailMessage);

    }
}
