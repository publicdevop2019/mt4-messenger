package com.hw.service;

import com.hw.clazz.GmailExpcetion;
import com.hw.entity.DeliverTask;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import javax.annotation.PostConstruct;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * merge similar deliver tasks
 */
@Service
@Slf4j
public class ShopAdminNotificationService extends MergeSimilarDeliverService {

    @PostConstruct
    private void initParam() {
        type = "Shop";
        debounceTime = 300 * 1000L;
    }

    @Autowired
    private JavaMailSender sender;

    @Autowired
    private Configuration freemarkerConfig;

    @Autowired
    private OAuthService oAuthService;

    @Override
    public void saveDeliverRequest(Map<String, String> map) {
        String adminList = oAuthService.getAdminList();
        if (adminList != null && !adminList.equals("")) {
            String[] split = adminList.split(",");
            for (String email : split) {
                DeliverTask shopAdmin = DeliverTask.create(email, type);
                deliverTaskRepo.save(shopAdmin);
            }
        } else {
            log.warn("no admin found for this notification service");
        }
    }

    @Override
    @Scheduled(fixedRateString = "${fixedRate.in.milliseconds.shop}")
    public void deliver() {
        List<DeliverTask> deliverTasks = scanPendingDeliverTask(debounceTime, type);
        log.info("scheduled deliver found {} deliver task to execute", deliverTasks.size());
        deliverTasks.forEach(deliverTask -> {
            MimeMessage message = sender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(message);
            Map<String, Object> model = new HashMap();
            Template t;
            try {
                t = freemarkerConfig.getTemplate("NewOrderEmailTemplate.ftl");
                String text = FreeMarkerTemplateUtils.processTemplateIntoString(t, model);
                mimeMessageHelper.setTo(deliverTask.getDeliverTo());
                mimeMessageHelper.setText(text, true); // set to html
                mimeMessageHelper.setSubject("New Order(s) Has Been Placed");
                log.info("start of sending gmail to subscriber");
                sender.send(message);
                log.info("sending gmail to subscriber success");
                deliverTask.onMsgDeliverSuccess(deliverTaskRepo);
            } catch (IOException | TemplateException | MessagingException e) {
                deliverTask.onMsgDeliverFailure(debounceTime, e.getCause().toString(), deliverTaskRepo);
                log.error("error when trying to send email::", e);
                throw new GmailExpcetion("error when trying to send email");
            }
        });


    }


}
