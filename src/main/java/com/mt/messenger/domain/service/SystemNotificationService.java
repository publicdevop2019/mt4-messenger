package com.mt.messenger.domain.service;

import com.mt.common.idempotent.HangingTxDetected;
import com.mt.messenger.domain.DomainRegistry;
import com.mt.messenger.domain.model.system_notification.SystemNotification;
import com.mt.messenger.domain.model.system_notification.SystemNotificationId;
import org.springframework.stereotype.Service;

@Service
public class SystemNotificationService {
    public SystemNotificationId create(HangingTxDetected deserialize) {
        SystemNotification systemNotification = new SystemNotification(deserialize);
        DomainRegistry.systemNotificationRepository().add(systemNotification);
        return systemNotification.getSystemNotificationId();
    }
}
