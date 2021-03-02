package com.mt.messenger.domain;

import com.mt.messenger.domain.model.mall_notification.MallNotificationRepository;
import com.mt.messenger.domain.model.system_notification.SystemNotificationRepository;
import com.mt.messenger.domain.service.MallNotificationService;
import com.mt.messenger.domain.service.SystemNotificationService;
import com.mt.messenger.domain.service.UserNotificationService;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DomainRegistry {
    @Getter
    private static UserNotificationService userNotificationService;
    @Getter
    private static SystemNotificationService systemNotificationService;
    @Getter
    private static SystemNotificationRepository systemNotificationRepository;
    @Getter
    private static MallNotificationRepository mallNotificationRepository;
    @Getter
    private static MallNotificationService mallNotificationService;

    @Autowired
    public void setMallNotificationRepository(MallNotificationRepository mallNotificationRepository) {
        DomainRegistry.mallNotificationRepository = mallNotificationRepository;
    }

    @Autowired
    public void setMallNotificationService(MallNotificationService mallNotificationService) {
        DomainRegistry.mallNotificationService = mallNotificationService;
    }

    @Autowired
    public void setUserNotificationService(UserNotificationService userNotificationService) {
        DomainRegistry.userNotificationService = userNotificationService;
    }

    @Autowired
    public void setSystemNotificationService(SystemNotificationService systemNotificationService) {
        DomainRegistry.systemNotificationService = systemNotificationService;
    }

    @Autowired
    public void setSystemNotificationRepository(SystemNotificationRepository systemNotificationRepository) {
        DomainRegistry.systemNotificationRepository = systemNotificationRepository;
    }

}
