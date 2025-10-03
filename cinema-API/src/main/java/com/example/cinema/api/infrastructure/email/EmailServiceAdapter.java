package com.example.cinema.api.infrastructure.email;

import com.example.cinema.api.domain.entities.MovieSession;
import com.example.cinema.api.domain.entities.Purchase;
import com.example.cinema.api.domain.entities.Ticket;
import com.example.cinema.api.domain.entities.User;
import com.example.cinema.api.domain.services.EmailServicePort;
import com.example.cinema.api.shared.exceptions.EmailSendException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Service
@Slf4j
public class EmailServiceAdapter implements EmailServicePort {

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine emailTemplateEngine;

    public EmailServiceAdapter(JavaMailSender mailSender, SpringTemplateEngine emailTemplateEngine) {
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
            log.error("Erro enviando e-mail HTML para {}", to, e);
            throw new EmailSendException("Não foi possível enviar e-mail HTML para " + to, e);
        }
    }

    @Override
    public void sendWelcomeEmail(String toEmail, String userName) {
        String subject = "Bem-vindo(a) ao CineMaster! 🎉";
        String templateName = "welcome-user";

        Context context = new Context(new Locale("pt", "BR"));
        context.setVariable("userName", userName);

        String htmlContent = emailTemplateEngine.process(templateName, context);

        sendHtmlMessage(toEmail, subject, htmlContent);
    }

    @Override
    public void sendPurchaseNotificationEmail(Purchase purchase) {
        User user = purchase.getUser();
        Ticket ticket = purchase.getTicket();
        MovieSession session = purchase.getMovieSession();

        Context context = new Context(new Locale("pt", "BR"));
        context.setVariable("userName", user.getName());
        context.setVariable("purchaseDate", purchase.getPurchaseDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        context.setVariable("totalPrice", purchase.getTotalPrice());
        context.setVariable("movieTitle", session.getMovie().getTitle());
        context.setVariable("sessionDateTime", session.getStartTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        context.setVariable("ticketSeat", ticket.getSeatNumber());

        String htmlContent = emailTemplateEngine.process("purchase-notification", context);
        String subject = "Confirmação da sua compra no CineMaster 🎫";

        sendHtmlMessage(user.getEmail(), subject, htmlContent);
    }
}
