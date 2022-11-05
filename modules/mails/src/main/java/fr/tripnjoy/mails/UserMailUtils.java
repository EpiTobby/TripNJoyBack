package fr.tripnjoy.mails;

import fr.tripnjoy.users.api.client.UserFeignClient;
import fr.tripnjoy.users.api.response.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Component;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.util.List;

@Component
public class UserMailUtils {

    public static final String USER_FIRST_NAME_PLACEHOLDER = "{userFirstName}";
    public static final List<String> ADMIN_CREDS = List.of("admin");
    private final MailConfigRecord config;
    private final MailSender mailSender;
    private final UserFeignClient userFeignClient;

    @Autowired
    private MessagesProperties messagesProperties;

    public UserMailUtils(final MailConfigRecord config, final MailSender mailSender,
                         final UserFeignClient userFeignClient) {
        this.config = config;
        this.mailSender = mailSender;
        this.userFeignClient = userFeignClient;
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

    public void sendConfirmationSuccessMail(long userId) {
        UserResponse user = userFeignClient.getUserById(ADMIN_CREDS, userId);
        SimpleMailMessage mail = new MailBuilder(config)
                .toAddr(user.getEmail())
                .setSubject(messagesProperties.getConfirmationSuccessMailSubject(user.getLanguage()))
                .setContent(messagesProperties.getConfirmationSuccessMailBody(user.getLanguage()).replace(USER_FIRST_NAME_PLACEHOLDER, user.getFirstname()))
                .build();
        mailSender.send(mail);
    }

    public void sendConfirmationCodeMail(long userId, String code) {
        UserResponse user = userFeignClient.getUserById(ADMIN_CREDS, userId);
        SimpleMailMessage mail = new MailBuilder(config)
                .toAddr(user.getEmail())
                .setSubject(messagesProperties.getConfirmationCodeSubject(user.getLanguage()))
                .setContent(messagesProperties.getConfirmationCodeBody(user.getLanguage()).replace(USER_FIRST_NAME_PLACEHOLDER, user.getFirstname()).replace("{code}", code))
                .build();
        mailSender.send(mail);
    }

    public void sendForgottenPasswordCodeMail(long userId, String code) {
        UserResponse user = userFeignClient.getUserById(ADMIN_CREDS, userId);
        SimpleMailMessage mail = new MailBuilder(config)
                .toAddr(user.getEmail())
                .setSubject(messagesProperties.getForgotPasswordSubject(user.getLanguage()))
                .setContent(messagesProperties.getForgotPasswordBody(user.getLanguage()).replace(USER_FIRST_NAME_PLACEHOLDER, user.getFirstname()).replace("{code}", code))
                .build();
        mailSender.send(mail);
    }

    public void sendUpdatePasswordMail(long userId) {
        UserResponse user = userFeignClient.getUserById(ADMIN_CREDS, userId);
        SimpleMailMessage mail = new MailBuilder(config)
                .toAddr(user.getEmail())
                .setSubject(messagesProperties.getUpdatePasswordSuccessSubject(user.getLanguage()))
                .setContent(messagesProperties.getUpdatePasswordSuccessBody(user.getLanguage()).replace(USER_FIRST_NAME_PLACEHOLDER, user.getFirstname()))
                .build();
        mailSender.send(mail);
    }

    public void sendDeleteAccountMail(long userId) {
        UserResponse user = userFeignClient.getUserById(ADMIN_CREDS, userId);
        SimpleMailMessage mail = new MailBuilder(config)
                .toAddr(user.getEmail())
                .setSubject(messagesProperties.getAccountDeletedSubject(user.getLanguage()))
                .setContent(messagesProperties.getAccountDeletedBody(user.getLanguage()).replace(USER_FIRST_NAME_PLACEHOLDER, user.getFirstname()))
                .build();
        mailSender.send(mail);
    }

    public void sendDeleteAccountByAdminMail(long userId, String reason) {
        UserResponse user = userFeignClient.getUserById(ADMIN_CREDS, userId);
        SimpleMailMessage mail = new MailBuilder(config)
                .toAddr(user.getEmail())
                .setSubject(messagesProperties.getAccountDeletedByAdminSubject(user.getLanguage()))
                .setContent(messagesProperties.getAccountDeletedByAdminBody(user.getLanguage()).replace(USER_FIRST_NAME_PLACEHOLDER, user.getFirstname()).replace("{reason}", reason))
                .build();
        mailSender.send(mail);
    }

    public void sendUpdateMail(long userId) {
        UserResponse user = userFeignClient.getUserById(ADMIN_CREDS, userId);
        SimpleMailMessage mail = new MailBuilder(config)
                .toAddr(user.getEmail())
                .setSubject(messagesProperties.getEmailUpdateSubject(user.getLanguage()))
                .setContent(messagesProperties.getEmailUpdateBody(user.getLanguage()).replace(USER_FIRST_NAME_PLACEHOLDER, user.getFirstname()))
                .build();
        mailSender.send(mail);
    }
}
