package project.iw3.iw3.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import project.iw3.iw3.model.business.exceptions.BusinessException;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class EmailBusiness {

    @Autowired
    private JavaMailSender emailSender;

    public void sendSimpleMessage(String to, String subject, String text) throws BusinessException {
        log.trace("Enviando mail subject={} a: {}", subject, to);

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("noreply@gmail.com");
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);

            emailSender.send(message);

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder()
                    .ex(e)
                    .message(e.getMessage())
                    .build();
        }
    }
}
