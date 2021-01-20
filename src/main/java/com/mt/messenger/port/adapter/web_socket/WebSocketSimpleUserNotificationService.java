package com.mt.messenger.port.adapter.web_socket;

import com.mt.messenger.domain.service.UserNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class WebSocketSimpleUserNotificationService implements UserNotificationService {
    @Autowired
    SpringBootSimpleWebSocketConfig.SocketTextHandler socketTextHandler;
    @Override
    public void notify(String message) {
        socketTextHandler.broadcast(message);
    }
}
