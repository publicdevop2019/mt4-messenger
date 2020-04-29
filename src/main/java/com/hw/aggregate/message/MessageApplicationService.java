package com.hw.aggregate.message;

import com.hw.aggregate.message.exception.CoolDownException;
import com.hw.aggregate.message.exception.GmailDeliverException;
import com.hw.aggregate.message.model.BizTypeEnum;
import com.hw.aggregate.message.model.Message;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.persistence.EntityManager;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
public class MessageApplicationService {
    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private JavaMailSender sender;

    @Autowired
    private Configuration freemarkerConfig;

    @Autowired
    private OAuthService oAuthService;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Autowired
    private EntityManager entityManager;

    public void sendPwdResetEmail(Map<String, String> map) {
        MimeMessage message = sender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(message);
        Map<String, Object> model = new HashMap<>();
        model.put("token", map.get("token"));
        Template t;
        try {
            t = freemarkerConfig.getTemplate("PasswordResetTemplate.ftl");
            String text = FreeMarkerTemplateUtils.processTemplateIntoString(t, model);
            mimeMessageHelper.setTo(map.get("email"));
            mimeMessageHelper.setText(text, true);
            mimeMessageHelper.setSubject("Your password reset token");
            sender.send(message);
        } catch (IOException | TemplateException | MessagingException e) {
            throw new GmailDeliverException();
        }

    }

    public void sendActivationCodeEmail(Map<String, String> map) {
        MimeMessage message = sender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(message);
        Map<String, Object> model = new HashMap<>();
        model.put("activationCode", map.get("activationCode"));
        Template t;
        try {
            t = freemarkerConfig.getTemplate("ActivationCodeTemplate.ftl");
            String text = FreeMarkerTemplateUtils.processTemplateIntoString(t, model);
            mimeMessageHelper.setTo(map.get("email"));
            mimeMessageHelper.setText(text, true);
            mimeMessageHelper.setSubject("Your activation code");
            sender.send(message);
        } catch (IOException | TemplateException | MessagingException e) {
            throw new GmailDeliverException();
        }
    }

    /**
     * 1.
     */
    @Transactional
    public void sendNewOrderEmail() {
        log.info("start of send email for new order");
        String adminEmail = oAuthService.getAdminList();
        Optional<Message> byDeliverTo = messageRepository.findByDeliverToAndBizType(adminEmail, BizTypeEnum.NEW_ORDER);
        if (byDeliverTo.isPresent()) {
            log.info("message was sent for {} before", adminEmail);
            Boolean aBoolean = byDeliverTo.get().hasCoolDown();
            if (!aBoolean)
                throw new CoolDownException();
            log.info("message has cool down");
            Message message = byDeliverTo.get();
            deliverEmail(adminEmail);
            message.onMsgSendSuccess();
            log.info("updating message status after email deliver");
            messageRepository.saveAndFlush(message);
        } else {
            log.info("new message for {}", adminEmail);
            Message message = Message.create(adminEmail, BizTypeEnum.NEW_ORDER);
            log.info("save to db first for concurrency scenario");
            messageRepository.saveAndFlush(message);
            continueDeliver(adminEmail);
        }

    }

    /**
     * manually create new transaction as this is call internally
     *
     * @param adminEmail
     */
    private void continueDeliver(String adminEmail) {
        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                Optional<Message> byDeliverTo = messageRepository.findByDeliverToAndBizType(adminEmail, BizTypeEnum.NEW_ORDER);
                if (byDeliverTo.isPresent()) {
                    log.info("message was sent for {} before", adminEmail);
                    Boolean aBoolean = byDeliverTo.get().hasCoolDown();
                    if (!aBoolean)
                        throw new CoolDownException();
                    log.info("message has cool down");
                    Message message = byDeliverTo.get();
                    deliverEmail(adminEmail);
                    log.info("updating message status after email deliver");
                    message.onMsgSendSuccess();
                    entityManager.persist(byDeliverTo.get());
                }
            }
        });
    }

    private void deliverEmail(String to) {
        MimeMessage mimeMessage = sender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
        Map<String, Object> model = new HashMap<>();
        Template t;
        try {
            t = freemarkerConfig.getTemplate("NewOrderEmailTemplate.ftl");
            String text = FreeMarkerTemplateUtils.processTemplateIntoString(t, model);
            mimeMessageHelper.setTo(to);
            mimeMessageHelper.setText(text, true); // set to html
            mimeMessageHelper.setSubject("New Order(s) Has Been Placed");
            sender.send(mimeMessage);
        } catch (IOException | TemplateException | MessagingException e) {
            throw new GmailDeliverException();
        }
    }
}
