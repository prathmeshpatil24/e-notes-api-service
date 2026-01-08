package com.enotes.utils.service;

import jakarta.mail.MessagingException;

public interface EmailService {

    void mimeEmailForm(String toEmail, String subject, String body) throws MessagingException;
}
