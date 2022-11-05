package fr.tripnjoy.mails;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessagePreparator;

import javax.mail.internet.MimeMessage;

@Configuration
@ConditionalOnProperty(name = "tripnjoy.mail.enable", havingValue = "false")
public class DebugMailConfiguration {
    @Bean
    MailConfigRecord config()
    {
        return new MailConfigRecord("debug@tripnjoy.com");
    }

    @Bean
    public JavaMailSender getJavaMailSender(@Value("${spring.mail.password}") String mailPassword)
    {
        return new PassThroughJavaMailSender();
    }
}

class PassThroughJavaMailSender extends JavaMailSenderImpl {
    @Override
    public void send(final SimpleMailMessage simpleMessage) throws MailException
    {
        // Pass through implementation
    }

    @Override
    public void send(final SimpleMailMessage... simpleMessages) throws MailException
    {
        // Pass through implementation
    }

    @Override
    public void send(final MimeMessage mimeMessage) throws MailException
    {
        // Pass through implementation
    }

    @Override
    public void send(final MimeMessage... mimeMessages) throws MailException
    {
        // Pass through implementation
    }

    @Override
    public void send(final MimeMessagePreparator mimeMessagePreparator) throws MailException
    {
        // Pass through implementation
    }

    @Override
    public void send(final MimeMessagePreparator... mimeMessagePreparators) throws MailException
    {
        // Pass through implementation
    }

    @Override
    protected void doSend(final MimeMessage[] mimeMessages, final Object[] originalMessages) throws MailException
    {
        // Pass through implementation
    }
}