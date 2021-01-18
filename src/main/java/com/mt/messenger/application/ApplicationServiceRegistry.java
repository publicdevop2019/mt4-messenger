package com.mt.messenger.application;

import com.mt.common.idempotent.ApplicationServiceIdempotentWrapper;
import com.mt.messenger.application.email_delivery.EmailDeliveryApplicationService;
import com.mt.messenger.application.system_notification.SystemNotificationApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ApplicationServiceRegistry {
    private static EmailDeliveryApplicationService emailDeliverApplicationService;
    private static SystemNotificationApplicationService systemNotificationApplicationService;
    private static ApplicationServiceIdempotentWrapper applicationServiceIdempotentWrapper;
    @Autowired
    public void setEmailDeliverApplicationService(EmailDeliveryApplicationService emailDeliverApplicationService) {
        ApplicationServiceRegistry.emailDeliverApplicationService = emailDeliverApplicationService;
    }

    @Autowired
    public void setSystemNotificationApplicationService(SystemNotificationApplicationService systemNotificationApplicationService) {
        ApplicationServiceRegistry.systemNotificationApplicationService = systemNotificationApplicationService;
    }

    @Autowired
    public void setClientIdempotentApplicationService(ApplicationServiceIdempotentWrapper clientIdempotentApplicationService) {
        ApplicationServiceRegistry.applicationServiceIdempotentWrapper = clientIdempotentApplicationService;
    }

    public static SystemNotificationApplicationService systemNotificationApplicationService() {
        return systemNotificationApplicationService;
    }


    public static EmailDeliveryApplicationService emailDeliverApplicationService() {
        return emailDeliverApplicationService;
    }
    public static ApplicationServiceIdempotentWrapper idempotentWrapper() {
        return applicationServiceIdempotentWrapper;
    }
}
