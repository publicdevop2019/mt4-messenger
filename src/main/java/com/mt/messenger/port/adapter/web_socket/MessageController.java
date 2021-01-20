package com.mt.messenger.port.adapter.web_socket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Slf4j
//@Controller
public class MessageController {
    @Autowired
    private SimpMessagingTemplate template;

    @MessageMapping("/notification")
    public void sendTopicMessage(String string) {
        template.convertAndSend("notify", string);
    }
}
