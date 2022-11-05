package fr.tripnjoy.mails;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:fr-fr_messages.properties")
@PropertySource("classpath:en-us_messages.properties")
public class MessagesProperties {
    public static final String FRENCH = "FRENCH";
    @Value("${fr-confirmation-success-mail-subject}")
    private String frConfirmationSuccessMailSubject;

    @Value("${fr-confirmation-success-mail-body}")
    private String frConfirmationSuccessMailBody;

    @Value("${fr-confirmation-code-subject}")
    private String frConfirmationCodeSubject;

    @Value("${fr-confirmation-code-body}")
    private String frConfirmationCodeBody;

    @Value("${fr-forgot-password-subject}")
    private String frForgotPasswordSubject;

    @Value("${fr-forgot-password-body}")
    private String frForgotPasswordBody;

    @Value("${fr-password-update-success-subject}")
    private String frUpdatePasswordSuccessSubject;

    @Value("${fr-password-update-success-body}")
    private String frUpdatePasswordSuccessBody;

    @Value("${fr-account-deleted-subject}")
    private String frAccountDeletedSubject;

    @Value("${fr-account-deleted-body}")
    private String frAccountDeletedBody;

    @Value("${fr-account-deleted-by-admin-subject}")
    private String frAccountDeletedByAdminSubject;

    @Value("${fr-account-deleted-by-admin-body}")
    private String frAccountDeletedByAdminBody;

    @Value("${fr-email-update-subject}")
    private String frEmailUpdateSubject;

    @Value("${fr-email-update-body}")
    private String frEmailUpdateBody;

    @Value("${en-confirmation-success-mail-subject}")
    private String enConfirmationSuccessMailSubject;

    @Value("${en-confirmation-success-mail-body}")
    private String enConfirmationSuccessMailBody;

    @Value("${en-confirmation-code-subject}")
    private String enConfirmationCodeSubject;

    @Value("${en-confirmation-code-body}")
    private String enConfirmationCodeBody;

    @Value("${en-forgot-password-subject}")
    private String enForgotPasswordSubject;

    @Value("${en-forgot-password-body}")
    private String enForgotPasswordBody;

    @Value("${en-password-update-success-subject}")
    private String enUpdatePasswordSuccessSubject;

    @Value("${en-password-update-success-body}")
    private String enUpdatePasswordSuccessBody;

    @Value("${en-account-deleted-subject}")
    private String enAccountDeletedSubject;

    @Value("${en-account-deleted-body}")
    private String enAccountDeletedBody;

    @Value("${en-account-deleted-by-admin-subject}")
    private String enAccountDeletedByAdminSubject;

    @Value("${en-account-deleted-by-admin-body}")
    private String enAccountDeletedByAdminBody;

    @Value("${en-email-update-subject}")
    private String enEmailUpdateSubject;

    @Value("${en-email-update-body}")
    private String enEmailUpdateBody;

    public String getConfirmationSuccessMailSubject(String language) {
        if (language.equals(FRENCH))
            return frConfirmationSuccessMailSubject;
        else
            return enConfirmationSuccessMailSubject;
    }

    public String getConfirmationSuccessMailBody(String language) {
        if (language.equals(FRENCH))
            return frConfirmationSuccessMailBody;
        else
            return enConfirmationSuccessMailBody;
    }

    public String getConfirmationCodeSubject(String language) {
        if (language.equals(FRENCH))
            return frConfirmationCodeSubject;
        else
            return enConfirmationCodeSubject;
    }

    public String getConfirmationCodeBody(String language) {
        if (language.equals(FRENCH))
            return frConfirmationCodeBody;
        else
            return enConfirmationCodeBody;
    }

    public String getForgotPasswordSubject(String language) {
        if (language.equals(FRENCH))
            return frForgotPasswordSubject;
        else
            return enForgotPasswordSubject;
    }

    public String getForgotPasswordBody(String language) {
        if (language.equals(FRENCH))
            return frForgotPasswordBody;
        else
            return enForgotPasswordBody;
    }

    public String getUpdatePasswordSuccessSubject(String language) {
        if (language.equals(FRENCH))
            return frUpdatePasswordSuccessSubject;
        else
            return enUpdatePasswordSuccessSubject;
    }

    public String getUpdatePasswordSuccessBody(String language) {
        if (language.equals(FRENCH))
            return frUpdatePasswordSuccessBody;
        else
            return enUpdatePasswordSuccessBody;
    }

    public String getAccountDeletedSubject(String language) {
        if (language.equals(FRENCH))
            return frAccountDeletedSubject;
        else
            return enAccountDeletedSubject;
    }

    public String getAccountDeletedBody(String language) {
        if (language.equals(FRENCH))
            return frAccountDeletedBody;
        else
            return enAccountDeletedBody;
    }

    public String getAccountDeletedByAdminSubject(String language) {
        if (language.equals(FRENCH))
            return frAccountDeletedByAdminSubject;
        else
            return enAccountDeletedByAdminSubject;
    }

    public String getAccountDeletedByAdminBody(String language) {
        if (language.equals(FRENCH))
            return frAccountDeletedByAdminBody;
        else
            return enAccountDeletedByAdminBody;
    }

    public String getEmailUpdateSubject(String language) {
        if (language.equals(FRENCH))
            return frEmailUpdateSubject;
        else
            return enEmailUpdateSubject;
    }

    public String getEmailUpdateBody(String language) {
        if (language.equals(FRENCH))
            return frEmailUpdateBody;
        else
            return enEmailUpdateBody;
    }
}
