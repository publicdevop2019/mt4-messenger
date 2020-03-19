package com.hw.service;

import com.hw.clazz.GmailExpcetion;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class RegisterNotificationService implements DeliverRightAwayService {

    @Autowired
    private JavaMailSender sender;

    @Autowired
    private Configuration freemarkerConfig;

    @Override
    public void deliver(Map<String, String> map) {
        MimeMessage message = sender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(message);
        Map<String, Object> model = new HashMap();
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
            log.error("error when trying to send email::", e);
            throw new GmailExpcetion("error when trying to send email");
        }

    }
}
