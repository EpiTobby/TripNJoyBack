package fr.tobby.tripnjoyback.mail;

import org.springframework.mail.SimpleMailMessage;

import java.util.ArrayList;
import java.util.List;

public final class MailBuilder {
    private final MailConfigRecord config;
    private final List<String> to = new ArrayList<>();
    private String subject;
    private String content;

    MailBuilder(final MailConfigRecord config)
    {
        this.config = config;
    }

    public SimpleMailMessage build()
    {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(config.fromAddr());
        message.setTo(to.toArray(new String[0]));
        message.setText(content);
        message.setSubject(subject);
        return message;
    }

    public MailBuilder toAddr(final String addr)
    {
        this.to.add(addr);
        return this;
    }

    public MailBuilder setSubject(final String subject)
    {
        this.subject = subject;
        return this;
    }

    public MailBuilder setContent(final String content)
    {
        this.content = content;
        return this;
    }
}
