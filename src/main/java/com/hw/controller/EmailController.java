package com.hw.controller;

import com.hw.service.OAuthService;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(value = "v1/api", produces = "application/json")
@Slf4j
public class EmailController {

    @Autowired
    private JavaMailSender sender;

    @Autowired
    private Configuration freemarkerConfig;

    @Autowired
    private OAuthService oAuthService;

    @PostMapping("notifyBy/email/newOrder")
    public ResponseEntity<?> sendOrderInfoToAccountViaEmail(@RequestBody Map<String, String> contentMap) {
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
                sender.send(message);
            } catch (IOException | TemplateException | MessagingException e) {
                e.printStackTrace();
                log.error("error when trying to send email::", e);
            }
        }
        return ResponseEntity.ok().build();
    }
}


