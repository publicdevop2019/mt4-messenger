package com.mt.messenger.port.adapter.messaging;

import com.mt.common.domain.model.CommonDomainRegistry;
import com.mt.messenger.model.ApplicationServiceRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RabbitMQEventListener {
    private static final String MESSENGER_QUEUE = "messenger_queue";

    @EventListener(ApplicationReadyEvent.class)
    protected void listener() {
        CommonDomainRegistry.eventStreamService().subscribe("oauth", false, MESSENGER_QUEUE, (event) -> {
            ApplicationServiceRegistry.messageApplicationService().handleEvent(event);
        }, "pendingUser", "user");
    }
}
