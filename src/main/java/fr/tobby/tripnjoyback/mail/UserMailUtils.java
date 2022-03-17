package fr.tobby.tripnjoyback.mail;

import fr.tobby.tripnjoyback.model.UserCreationModel;
import fr.tobby.tripnjoyback.model.UserModel;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Component;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

@Component
public class UserMailUtils {

    private final MailConfigRecord config;
    private final MailSender mailSender;

    public UserMailUtils(final MailConfigRecord config, final MailSender mailSender)
    {
        this.config = config;
        this.mailSender = mailSender;
    }

    public boolean userEmailExists(String email){
        try {
            InternetAddress internetAddress = new InternetAddress(email);
            internetAddress.validate();
            return true;
        }
        catch (AddressException e){
            return false;
        }
    }
    public void sendConfirmationSuccessMail(UserModel user)
    {
        SimpleMailMessage mail = new MailBuilder(config)
                .toAddr(user.getEmail())
                .setSubject("Confirmation de la création de votre compte TripNJoy")
                .setContent("Bonjour " + user.getFirstname() + ",\n\nBienvenue dans notre application.\nCordialement,\nl'équipe TripNJoy")
                .build();
        mailSender.send(mail);
    }

    public void sendConfirmationCodeMail(UserModel user, String code)
    {
        SimpleMailMessage mail = new MailBuilder(config)
                .toAddr(user.getEmail())
                .setSubject("Code de confirmation de votre compte TripNJoy")
                .setContent("Bonjour " + user.getFirstname() + ",\n\nVoici votre code de confirmation: "
                        + code + "\nCe dernier expirera dans 24 heures.\nCordialement,\nl'équipe TripNJoy")
                .build();
        mailSender.send(mail);
    }

    public void sendForgottenPasswordCodeMail(UserModel user, String code)
    {
        SimpleMailMessage mail = new MailBuilder(config)
                .toAddr(user.getEmail())
                .setSubject("Code de changement de mot de passe TripNJoy")
                .setContent("Bonjour " + user.getFirstname() + ",\n\nVoici votre code de changement de mot de passe: "
                        + code + "\nCe dernier expirera dans 24 heures.\nCordialement,\nl'équipe TripNJoy")
                .build();
        mailSender.send(mail);
    }

    public void sendUpdatePasswordMail(UserModel user)
    {
        SimpleMailMessage mail = new MailBuilder(config)
                .toAddr(user.getEmail())
                .setSubject("Confirmation du changement de mot de passe TripNJoy")
                .setContent("Bonjour " + user.getFirstname() + ",\n\nVotre mot de passe a bien été mis à jour.\nCordialement,\nl'équipe TripNJoy")
                .build();
        mailSender.send(mail);
    }



    public void sendUpdateMail(UserModel user){
        SimpleMailMessage mail = new MailBuilder(config)
                .toAddr(user.getEmail())
                .setSubject("Confirmation du changement d'adresse email TripNJoy")
                .setContent("Bonjour " + user.getFirstname() + ",\n\nVotre compte TripNJoy est à présent lié à cette adresse email.\nCordialement,\nl'équipe TripNJoy")
                .build();
        mailSender.send(mail);
    }
}
