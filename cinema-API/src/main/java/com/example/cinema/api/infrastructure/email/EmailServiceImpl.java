package com.example.cinema.api.infrastructure.email;

import com.example.cinema.api.domain.entities.Purchase;
import com.example.cinema.api.shared.exceptions.EmailSendException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl {

    private static final Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);
    private final JavaMailSender mailSender;
    private final String from;

    public EmailServiceImpl(JavaMailSender mailSender,
                            @Value("${spring.mail.username}") String from) {
        this.mailSender = mailSender;
        this.from = from;
    }

    private void sendSimpleMessage(String to, String subject, String text) {
        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setFrom(from);
            msg.setTo(to);
            msg.setSubject(subject);
            msg.setText(text);
            mailSender.send(msg);
        } catch (MailException e) {
            logger.error("Erro enviando e-mail para {}", to, e);
            throw new EmailSendException("Não foi possível enviar e-mail para " + to, e);
        }
    }

    public void sendPurchaseNotificationEmail(String to, Purchase purchase) {
        String subject = "Sua compra foi confirmada! 🍿🎬";
        String text = String.format(
                "Olá %s,%n%n" +
                        "Sua compra para o filme \"%s\" foi realizada com sucesso!%n%n" +
                        "Detalhes da compra:%n" +
                        "Sessão: %s%n" +
                        "Valor Total: R$ %.2f%n" +
                        "Data da Compra: %s%n%n" +
                        "Prepare a pipoca e aproveite o filme!%n%n" +
                        "Obrigado por escolher nosso cinema online.%n" +
                        "Até breve!%n%n" +
                        "Atenciosamente,%n" +
                        "Equipe Cinema Online",
                purchase.getUser().getName(),
                purchase.getMovieSession().getMovie().getTitle(),
                purchase.getMovieSession().getStartTime(),
                purchase.getTotalPrice(),
                purchase.getPurchaseDate()
        );

        sendSimpleMessage(to, subject, text);
    }

    public void sendWelcomeEmail(String to, String userName) {
        String subject = "Bem-vindo(a) ao Cinema Online! 🎉";
        String text = String.format(
                "Olá %s,%n%n" +
                        "Obrigado por se cadastrar no Cinema Online, seu portal para as melhores sessões!%n%n" +
                        "Agora você pode comprar ingressos, acompanhar lançamentos e receber ofertas exclusivas.%n%n" +
                        "Esperamos que aproveite muito a experiência.%n%n" +
                        "Seja muito bem-vindo(a)!%n%n" +
                        "Atenciosamente,%n" +
                        "Equipe Cinema Online",
                userName
        );

        sendSimpleMessage(to, subject, text);
    }
}

