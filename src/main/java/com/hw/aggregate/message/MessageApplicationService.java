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

    @Transactional
    public void sendPwdResetEmail(Map<String, String> map) {
        log.info("start of send email for pwd reset");
        Map<String, Object> model = new HashMap<>();
        model.put("token", map.get("token"));
        sendEmail(map.get("email"), "PasswordResetTemplate.ftl", "Your password reset token", model);
    }

    @Transactional
    public void sendActivationCodeEmail(Map<String, String> map) {
        log.info("start of send email for activation code");
        Map<String, Object> model = new HashMap<>();
        model.put("activationCode", map.get("activationCode"));
        sendEmail(map.get("email"), "ActivationCodeTemplate.ftl", "Your activation code", model);
    }

    @Transactional
    public void sendNewOrderEmail() {
        log.info("start of send email for new order");
        String adminEmail = oAuthService.getAdminList();
        sendEmail(adminEmail, "NewOrderEmailTemplate.ftl", "New Order(s) Has Been Placed", new HashMap<>());
    }

    private void sendEmail(String email, String templateUrl, String subject, Map<String, Object> model) {
        Optional<Message> byDeliverTo = messageRepository.findByDeliverToAndBizType(email, BizTypeEnum.NEW_ORDER);
        if (byDeliverTo.isPresent()) {
            continueDeliverShared(email, byDeliverTo.get(), templateUrl, subject, model);
            messageRepository.saveAndFlush(byDeliverTo.get());
        } else {
            log.info("new message for {}", email);
            Message message = Message.create(email, BizTypeEnum.NEW_ORDER);
            log.info("save to db first for concurrency scenario");
            messageRepository.saveAndFlush(message);
            continueDeliver(email, templateUrl, subject, model);
        }
    }

    /**
     * manually create new transaction as this is call internally
     *
     * @param email
     */
    private void continueDeliver(String email, String templateUrl, String subject, Map<String, Object> model) {
        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                Optional<Message> byDeliverTo = messageRepository.findByDeliverToAndBizType(email, BizTypeEnum.NEW_ORDER);
                if (byDeliverTo.isPresent()) {
                    continueDeliverShared(email, byDeliverTo.get(), templateUrl, subject, model);
                    entityManager.persist(byDeliverTo.get());
                }
            }
        });
    }

    private void deliverEmail(String to, String templateUrl, String subject, Map<String, Object> model) {
        log.info("deliver email");
        MimeMessage mimeMessage = sender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
        Template t;
        try {
            t = freemarkerConfig.getTemplate(templateUrl);
            String text = FreeMarkerTemplateUtils.processTemplateIntoString(t, model);
            mimeMessageHelper.setTo(to);
            mimeMessageHelper.setText(text, true); // set to html
            mimeMessageHelper.setSubject(subject);
            sender.send(mimeMessage);
        } catch (IOException | TemplateException | MessagingException e) {
            throw new GmailDeliverException();
        }
    }

    private void continueDeliverShared(String email, Message message, String templateUrl, String subject, Map model) {
        log.info("message was sent for {} before", email);
        Boolean aBoolean = message.hasCoolDown();
        if (!aBoolean)
            throw new CoolDownException();
        log.info("message has cool down");
        deliverEmail(email, templateUrl, subject, model);
        log.info("updating message status after email deliver");
        message.onMsgSendSuccess();
    }
}
