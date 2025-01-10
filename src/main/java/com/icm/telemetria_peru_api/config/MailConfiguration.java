package com.icm.telemetria_peru_api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

/**
 * Configuration class for setting up the mail sender using Spring's JavaMailSender.
 * This configuration uses Gmail's SMTP server to send emails.
 *
 * <p>
 * The email configuration properties (`email.sender` and `email.password`) are injected
 * from the application properties or environment variables.
 * </p>
 *
 * <p>
 * The mail sender is configured with SMTP settings including:
 * <ul>
 *   <li>SMTP host: smtp.gmail.com</li>
 *   <li>SMTP port: 587</li>
 *   <li>SMTP authentication: true</li>
 *   <li>StartTLS: enabled</li>
 *   <li>Debug mode: disabled</li>
 * </ul>
 * </p>
 *
 * <p>
 * This configuration provides the JavaMailSender bean which can be injected into other components
 * to send emails using Gmail's SMTP service.
 * </p>
 */
@Configuration
public class MailConfiguration {
    @Value("${email.sender}")
    private String emailUser;

    @Value("${email.password}")
    private String password;

    @Bean
    public JavaMailSender getJavaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(587);
        mailSender.setUsername(emailUser);
        mailSender.setPassword(password);

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "false");

        return mailSender;
    }
}
