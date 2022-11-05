package fr.tripnjoy.controller;

import fr.tripnjoy.common.dto.BooleanResponse;
import fr.tripnjoy.mails.UserMailUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MailController {

    private final UserMailUtils mails;

    public MailController(final UserMailUtils mails)
    {
        this.mails = mails;
    }

    @GetMapping("valid")
    public BooleanResponse userEmailIsValid(@RequestParam("email") String email)
    {
        return new BooleanResponse(mails.userEmailIsValid(email));
    }

    @GetMapping("sendConfirmationSuccessMail")
    public void sendConfirmationSuccessMail(@RequestParam("userId") long userId)
    {
        mails.sendConfirmationSuccessMail(userId);
    }

    @GetMapping("sendConfirmationCodeMail")
    public void sendConfirmationCodeMail(@RequestParam("userId") long userId, @RequestParam("code") String code)
    {
        mails.sendConfirmationCodeMail(userId, code);
    }

    @GetMapping("sendForgottenPasswordCodeMail")
    public void sendForgottenPasswordCodeMail(@RequestParam("userId") long userId, @RequestParam("code") String code)
    {
        mails.sendForgottenPasswordCodeMail(userId, code);
    }

    @GetMapping("sendUpdatePasswordMail")
    public void sendUpdatePasswordMail(@RequestParam("userId") long userId)
    {
        mails.sendUpdatePasswordMail(userId);
    }

    @GetMapping("sendDeleteAccountMail")
    public void sendDeleteAccountMail(@RequestParam("userId") long userId)
    {
        mails.sendDeleteAccountMail(userId);
    }

    @GetMapping("sendDeleteAccountByAdminMail")
    public void sendDeleteAccountByAdminMail(@RequestParam("userId") long userId, @RequestParam("reason") String reason)
    {
        mails.sendDeleteAccountByAdminMail(userId, reason);
    }

    @GetMapping("sendUpdateMail")
    public void sendUpdateMail(@RequestParam("userId") long userId)
    {
        mails.sendUpdateMail(userId);
    }
}
