package com.mt.messenger.port.adapter.messaging;

import com.mt.common.domain.CommonDomainRegistry;
import com.mt.messenger.application.ApplicationServiceRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import static com.mt.common.domain.model.idempotent.event.HangingTxDetected.MONITOR_TOPIC;
import static com.mt.common.domain.model.idempotent.event.SkuChangeFailed.MALL_MONITOR_TOPIC;

@Slf4j
@Component
public class MessageQueueListener {
    private static final String MESSENGER_USER_QUEUE = "messenger_user_queue";
    private static final String MESSENGER_SYS_MONITOR_QUEUE = "messenger_sys_monitor_queue";
    private static final String MESSENGER_MALL_MONITOR_QUEUE = "messenger_mall_monitor_queue";

    @EventListener(ApplicationReadyEvent.class)
    protected void userNotificationListener() {
        CommonDomainRegistry.getEventStreamService().subscribe("oauth", false, MESSENGER_USER_QUEUE, (event) -> {
            ApplicationServiceRegistry.getEmailDeliverApplicationService().handleEvent(event);
        }, "pendingUser", "user");
    }

    @EventListener(ApplicationReadyEvent.class)
    protected void systemMonitorListener() {
        CommonDomainRegistry.getEventStreamService().subscribe("oauth", false, MESSENGER_SYS_MONITOR_QUEUE, (event) -> {
            ApplicationServiceRegistry.getSystemNotificationApplicationService().handleMonitorEvent(event);
        }, MONITOR_TOPIC);
    }

    @EventListener(ApplicationReadyEvent.class)
    protected void mallMonitorListener() {
        CommonDomainRegistry.getEventStreamService().subscribe("product", false, MESSENGER_MALL_MONITOR_QUEUE, (event) -> {
            ApplicationServiceRegistry.getMallNotificationApplicationService().handleMonitorEvent(event);
        }, MALL_MONITOR_TOPIC);
    }
}
