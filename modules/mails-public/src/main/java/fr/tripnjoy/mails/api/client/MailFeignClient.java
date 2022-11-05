package fr.tripnjoy.mails.api.client;

import fr.tripnjoy.common.dto.BooleanResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "SERVICE-MAILS", contextId = "SERVICE-MAILS-MAIL", path = "/")
public interface MailFeignClient {

    @GetMapping("valid")
    BooleanResponse userEmailIsValid(@RequestParam("email") String email);

    @GetMapping("sendConfirmationSuccessMail")
    void sendConfirmationSuccessMail(@RequestParam("userId") long userId);

    @GetMapping("sendConfirmationCodeMail")
    void sendConfirmationCodeMail(@RequestParam("userId") long userId, @RequestParam("code") String code);

    @GetMapping("sendForgottenPasswordCodeMail")
    void sendForgottenPasswordCodeMail(@RequestParam("userId") long userId, @RequestParam("code") String code);

    @GetMapping("sendUpdatePasswordMail")
    void sendUpdatePasswordMail(@RequestParam("userId") long userId);

    @GetMapping("sendDeleteAccountMail")
    void sendDeleteAccountMail(@RequestParam("userId") long userId);

    @GetMapping("sendDeleteAccountByAdminMail")
    void sendDeleteAccountByAdminMail(@RequestParam("userId") long userId, @RequestParam("reason") String reason);

    @GetMapping("sendUpdateMail")
    void sendUpdateMail(@RequestParam("userId") long userId);
}
