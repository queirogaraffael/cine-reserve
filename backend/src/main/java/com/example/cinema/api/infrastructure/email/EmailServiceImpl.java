package com.example.cinema.api.infrastructure.email;

import com.example.cinema.api.application.service.EmailService;
import com.example.cinema.api.application.dto.email.EmailVerificacaoNotificationData;
import com.example.cinema.api.application.dto.email.PaymentCardInitiatedNotificationData;
import com.example.cinema.api.application.dto.email.PurchaseCreatedNotificationData;
import com.example.cinema.api.application.dto.email.UserCreatedNotificationData;
import com.example.cinema.api.infrastructure.email.exception.EmailSendException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Service
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine emailTemplateEngine;

    public EmailServiceImpl(JavaMailSender mailSender, SpringTemplateEngine emailTemplateEngine) {
        this.mailSender = mailSender;
        this.emailTemplateEngine = emailTemplateEngine;
    }

    private void sendHtmlMessage(String to, String subject, String htmlBody) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);

            mailSender.send(message);
        } catch (MessagingException | MailException e) {
            throw new EmailSendException("Não foi possível enviar e-mail para " + to, e);
        }
    }

    @Override
    public void sendWelcomeEmailComVerificacao(UserCreatedNotificationData dto) {
        String subject = "Bem-vindo(a) ao CineMaster - Confirme seu e-mail!";
        String templateName = "welcome-user";

        Context context = new Context(new Locale("pt", "BR"));
        context.setVariable("userName", dto.getName());
        context.setVariable("codigoVerificacao", dto.getCodigoVerificacao());

        String htmlContent = emailTemplateEngine.process(templateName, context);

        sendHtmlMessage(dto.getEmail(), subject, htmlContent);
    }

    @Override
    public void sendEmailVerificacao(EmailVerificacaoNotificationData dto) {
        String subject = "Código de Verificação - CineMaster";
        String templateName = "verificacao-email";

        Context context = new Context(new Locale("pt", "BR"));
        context.setVariable("userName", dto.getName());
        context.setVariable("codigoVerificacao", dto.getCodigo());

        String htmlContent = emailTemplateEngine.process(templateName, context);

        sendHtmlMessage(dto.getEmail(), subject, htmlContent);
    }

    @Override
    public void notifyPurchaseCreatedEmail(PurchaseCreatedNotificationData dto) {
        Context context = new Context(new Locale("pt", "BR"));
        context.setVariable("userName", dto.getName());
        context.setVariable("purchaseDate", dto.getPurchaseDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        context.setVariable("totalPrice", dto.getTotalPrice());

        String htmlContent = emailTemplateEngine.process("purchase-created-event", context);
        String subject = "Confirmação da sua compra no CineMaster";

        sendHtmlMessage(dto.getEmail(), subject, htmlContent);
    }

    @Override
    public void notifyPaymentCardInitiatedEmail(PaymentCardInitiatedNotificationData dto) {
        Context context = new Context(new Locale("pt", "BR"));
        context.setVariable("userName", dto.getName());
        context.setVariable("paymentDate", dto.getPaymentDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        context.setVariable("totalPrice", dto.getTotalPrice());

        String htmlContent = emailTemplateEngine.process("payment-card-initiated", context);
        String subject = "Recebemos seu pagamento – aguardando confirmação";

        sendHtmlMessage(dto.getEmail(), subject, htmlContent);
    }
}
