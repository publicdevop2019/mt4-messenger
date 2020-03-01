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
public class EmailServiceImpl {
    @Autowired
    private JavaMailSender sender;

    @Autowired
    private Configuration freemarkerConfig;

    @Autowired
    private OAuthService oAuthService;

    public void sendOrderInfoToAccountViaEmail() {
        String adminList = oAuthService.getAdminList();
        String[] split = adminList.split(",");
        for (String email : split) {

            MimeMessage message = sender.createMimeMessage();

            MimeMessageHelper helper = new MimeMessageHelper(message);

            Map<String, Object> model = new HashMap();

            Template t = null;
            try {
                t = freemarkerConfig.getTemplate("NewOrderEmailTemplate.ftl");
                String text = FreeMarkerTemplateUtils.processTemplateIntoString(t, model);
                helper.setTo(email);
                helper.setText(text, true); // set to html
                helper.setSubject("New Order Has Been Placed");
                log.info("start of sending gmail to subscriber");
                sender.send(message);
                log.info("end of sending gmail to subscriber");
            } catch (IOException | TemplateException | MessagingException e) {
                e.printStackTrace();
                log.error("error when trying to send email::", e);
                throw new GmailExpcetion("error when trying to send email");
            }
        }
    }
}
