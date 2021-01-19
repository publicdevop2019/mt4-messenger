package com.mt.messenger.port.adapter.web_socket;

import com.mt.messenger.domain.service.UserNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class WebSocketUserNotificationService implements UserNotificationService {
    @Autowired
    private SimpMessagingTemplate template;
    @Override
    public void notify(String message) {
        template.convertAndSend("notify", message);
    }
}
