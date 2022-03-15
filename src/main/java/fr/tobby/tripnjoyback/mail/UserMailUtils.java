package fr.tobby.tripnjoyback.mail;

import fr.tobby.tripnjoyback.model.UserModel;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Component;

@Component
public class UserMailUtils {

    private final MailConfigRecord config;
    private final MailSender mailSender;

    public UserMailUtils(final MailConfigRecord config, final MailSender mailSender)
    {
        this.config = config;
        this.mailSender = mailSender;
    }

    public void sendConfirmationSuccessMail(UserModel user)
    {
        SimpleMailMessage mail = new MailBuilder(config)
                .toAddr(user.getEmail())
                .setSubject("Confirmation de la création de votre compte TripNJoy")
                .setContent("Bonjour " + user.getFirstname() + ",\n\nBienvenue dans notre application.\nCordialement, l'équipe TripNJoy")
                .build();
        mailSender.send(mail);
    }

    public void sendConfirmationCodeMail(UserModel user, String code)
    {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom("tripnjoy.contact@gmail.com");
        mailMessage.setTo(user.getEmail());
        mailMessage.setSubject("Code de confirmation TripNJoy");
        mailMessage.setText("Bonjour " + user.getFirstname() + ",\n\nVoici votre code de confirmation: "
                + code + "\nCe dernier expirera dans 24 heures.\nCordialement, l'équipe TripNJoy");
        mailSender.send(mailMessage);
    }
}
