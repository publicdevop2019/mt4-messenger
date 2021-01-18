package com.mt.messenger.domain;

import com.mt.messenger.domain.model.system_notification.SystemNotificationRepository;
import com.mt.messenger.domain.service.SystemNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DomainRegistry {
    private static SystemNotificationService systemNotificationService;
    private static SystemNotificationRepository systemNotificationRepository;

    @Autowired
    public void setSystemNotificationService(SystemNotificationService systemNotificationService) {
        DomainRegistry.systemNotificationService = systemNotificationService;
    }

    public static SystemNotificationService systemNotificationService() {
        return systemNotificationService;
    }

    @Autowired
    public void setSystemNotificationRepository(SystemNotificationRepository systemNotificationRepository) {
        DomainRegistry.systemNotificationRepository = systemNotificationRepository;
    }

    public static SystemNotificationRepository systemNotificationRepository() {
        return systemNotificationRepository;
    }
}
