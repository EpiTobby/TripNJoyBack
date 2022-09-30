package fr.tobby.tripnjoyback.mail;

import fr.tobby.tripnjoyback.model.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Component;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

@Component
public class UserMailUtils {

    public static final String USER_FIRST_NAME_PLACEHOLDER = "{userFirstName}";
    private final MailConfigRecord config;
    private final MailSender mailSender;

    @Autowired
    private MessagesProperties messagesProperties;

    public UserMailUtils(final MailConfigRecord config, final MailSender mailSender) {
        this.config = config;
        this.mailSender = mailSender;
    }

    public boolean userEmailIsValid(String email) {
        try {
            InternetAddress internetAddress = new InternetAddress(email);
            internetAddress.validate();
            return true;
        } catch (AddressException e) {
            return false;
        }
    }

    public void sendConfirmationSuccessMail(UserModel user) {
        SimpleMailMessage mail = new MailBuilder(config)
                .toAddr(user.getEmail())
                .setSubject(messagesProperties.getConfirmationSuccessMailSubject(user))
                .setContent(messagesProperties.getConfirmationSuccessMailBody(user).replace(USER_FIRST_NAME_PLACEHOLDER, user.getFirstname()))
                .build();
        mailSender.send(mail);
    }

    public void sendConfirmationCodeMail(UserModel user, String code) {
        SimpleMailMessage mail = new MailBuilder(config)
                .toAddr(user.getEmail())
                .setSubject(messagesProperties.getConfirmationCodeSubject(user))
                .setContent(messagesProperties.getConfirmationCodeBody(user).replace(USER_FIRST_NAME_PLACEHOLDER, user.getFirstname()).replace("{code}", code))
                .build();
        mailSender.send(mail);
    }

    public void sendForgottenPasswordCodeMail(UserModel user, String code) {
        SimpleMailMessage mail = new MailBuilder(config)
                .toAddr(user.getEmail())
                .setSubject(messagesProperties.getForgotPasswordSubject(user))
                .setContent(messagesProperties.getForgotPasswordBody(user).replace(USER_FIRST_NAME_PLACEHOLDER, user.getFirstname()).replace("{code}", code))
                .build();
        mailSender.send(mail);
    }

    public void sendUpdatePasswordMail(UserModel user) {
        SimpleMailMessage mail = new MailBuilder(config)
                .toAddr(user.getEmail())
                .setSubject(messagesProperties.getUpdatePasswordSuccessSubject(user))
                .setContent(messagesProperties.getUpdatePasswordSuccessBody(user).replace(USER_FIRST_NAME_PLACEHOLDER, user.getFirstname()))
                .build();
        mailSender.send(mail);
    }

    public void sendDeleteAccountMail(UserModel user) {
        SimpleMailMessage mail = new MailBuilder(config)
                .toAddr(user.getEmail())
                .setSubject(messagesProperties.getAccountDeletedSubject(user))
                .setContent(messagesProperties.getAccountDeletedBody(user).replace(USER_FIRST_NAME_PLACEHOLDER, user.getFirstname()))
                .build();
        mailSender.send(mail);
    }

    public void sendDeleteAccountByAdminMail(UserModel user, String reason) {
        SimpleMailMessage mail = new MailBuilder(config)
                .toAddr(user.getEmail())
                .setSubject(messagesProperties.getAccountDeletedByAdminSubject(user))
                .setContent(messagesProperties.getAccountDeletedByAdminBody(user).replace(USER_FIRST_NAME_PLACEHOLDER, user.getFirstname()).replace("{reason}", reason))
                .build();
        mailSender.send(mail);
    }

    public void sendUpdateMail(UserModel user) {
        SimpleMailMessage mail = new MailBuilder(config)
                .toAddr(user.getEmail())
                .setSubject(messagesProperties.getEmailUpdateSubject(user))
                .setContent(messagesProperties.getEmailUpdateBody(user).replace(USER_FIRST_NAME_PLACEHOLDER, user.getFirstname()))
                .build();
        mailSender.send(mail);
    }
}
