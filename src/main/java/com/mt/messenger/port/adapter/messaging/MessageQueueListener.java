package com.mt.messenger.port.adapter.messaging;

import com.mt.common.domain.model.CommonDomainRegistry;
import com.mt.messenger.application.ApplicationServiceRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import static com.mt.common.idempotent.HangingTxDetected.MONITOR_TOPIC;

@Slf4j
@Component
public class MessageQueueListener {
    private static final String MESSENGER_USER_QUEUE = "messenger_user_queue";
    private static final String MESSENGER_MONITOR_QUEUE = "messenger_monitor_queue";

    @EventListener(ApplicationReadyEvent.class)
    protected void userNotificationListener() {
        CommonDomainRegistry.eventStreamService().subscribe("oauth", false, MESSENGER_USER_QUEUE, (event) -> {
            ApplicationServiceRegistry.emailDeliverApplicationService().handleEvent(event);
        }, "pendingUser", "user");
    }

    @EventListener(ApplicationReadyEvent.class)
    protected void systemMonitorListener() {
        CommonDomainRegistry.eventStreamService().subscribe("oauth", false, MESSENGER_MONITOR_QUEUE, (event) -> {
            ApplicationServiceRegistry.systemNotificationApplicationService().handleMonitorEvent(event);
        }, MONITOR_TOPIC);
    }
}
