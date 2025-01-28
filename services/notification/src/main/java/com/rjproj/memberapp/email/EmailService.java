package com.rjproj.memberapp.email;

import com.rjproj.memberapp.kafka.event.ConfirmationStatus;
import com.rjproj.memberapp.kafka.event.Event;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.rjproj.memberapp.email.EmailTemplates.YES;
import static java.nio.charset.StandardCharsets.UTF_8;
@Service
@Slf4j
@RequiredArgsConstructor
public class EmailService {

    private JavaMailSender javaMailSender;

    private final SpringTemplateEngine templateEngine;

    @Async
    public void sendConfirmationSuccessfulEmail(
            UUID eventConfirmationId,
            Event event,
            UUID memberId,
            ConfirmationStatus confirmationStatus,
            Timestamp confirmationDate,
            Timestamp createdAt,
            Timestamp updatedAt
    ) throws MessagingException {

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, UTF_8.name());
        messageHelper.setFrom("arjay2489@gmail.com");
        String destinationEmail = "arjay630@gmail.com";

        final String templateName = YES.getTemplate();

        Map<String, Object> variables = new HashMap<>();
        variables.put("eventConfirmationId", eventConfirmationId);
        variables.put("event", event);
        variables.put("memberId", memberId);
        variables.put("confirmationStatus", confirmationStatus);
        variables.put("confirmationDate", confirmationDate);
        variables.put("createdAt", createdAt);
        variables.put("updatedAt", updatedAt);

        //variables.put("orderReference", orderReference);

        Context context = new Context();
        context.setVariables(variables);
        messageHelper.setSubject(YES.getSubject());

        try {
            String htmlTemplate = templateEngine.process(templateName, context);
            messageHelper.setText(htmlTemplate, true);

            messageHelper.setTo(destinationEmail);
            javaMailSender.send(mimeMessage);
            log.info(String.format("INFO - Email successfully sent to %s with template %s ", destinationEmail, templateName));
        } catch (MessagingException e) {
            log.warn("WARNING - Cannot send Email to {} ", destinationEmail);
        }

    }
}