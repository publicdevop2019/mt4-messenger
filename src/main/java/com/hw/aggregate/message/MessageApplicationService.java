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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
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

    @Transactional
    public void sendNewOrderEmail() {
        String admin = oAuthService.getAdminList();
        Optional<Message> byDeliverTo = messageRepository.findByDeliverToAndBizType(admin, BizTypeEnum.NEW_ORDER);
        if (byDeliverTo.isPresent()) {
            Boolean aBoolean = byDeliverTo.get().hasCoolDown();
            if (!aBoolean)
                throw new CoolDownException();
            Message message = byDeliverTo.get();
            try {
                // concurrency work around
                messageRepository.saveAndFlush(message);
            } catch (Exception ex) {
                log.error("value has been changed by other thread", ex);
            }
            MimeMessage mimeMessage = sender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
            Map<String, Object> model = new HashMap<>();
            Template t;
            try {
                t = freemarkerConfig.getTemplate("NewOrderEmailTemplate.ftl");
                String text = FreeMarkerTemplateUtils.processTemplateIntoString(t, model);
                mimeMessageHelper.setTo(admin);
                mimeMessageHelper.setText(text, true); // set to html
                mimeMessageHelper.setSubject("New Order(s) Has Been Placed");
                sender.send(mimeMessage);
                message.setLastTimeResult(Boolean.TRUE);
                messageRepository.saveAndFlush(message);
            } catch (IOException | TemplateException | MessagingException e) {
                throw new GmailDeliverException();
            }
        } else {
            Message message = Message.create(admin, BizTypeEnum.NEW_ORDER);
            MimeMessage mimeMessage = sender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
            Map<String, Object> model = new HashMap<>();
            Template t;
            try {
                t = freemarkerConfig.getTemplate("NewOrderEmailTemplate.ftl");
                String text = FreeMarkerTemplateUtils.processTemplateIntoString(t, model);
                mimeMessageHelper.setTo(admin);
                mimeMessageHelper.setText(text, true); // set to html
                mimeMessageHelper.setSubject("New Order(s) Has Been Placed");
                sender.send(mimeMessage);
                message.setLastTimeResult(Boolean.TRUE);
                messageRepository.saveAndFlush(message);
            } catch (IOException | TemplateException | MessagingException e) {
                throw new GmailDeliverException();
            }
        }

    }
}
