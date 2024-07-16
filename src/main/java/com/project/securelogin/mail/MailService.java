package com.project.securelogin.mail;

public interface MailService {
    void sendEmail(String to, String verificationUrl, String subject);
}
