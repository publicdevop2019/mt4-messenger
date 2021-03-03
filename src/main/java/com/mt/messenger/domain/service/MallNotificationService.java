package com.mt.messenger.domain.service;

import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.idempotent.event.SkuChangeFailed;
import com.mt.messenger.application.mall_notification.MallNotificationRepresentation;
import com.mt.messenger.domain.DomainRegistry;
import com.mt.messenger.domain.model.mall_notification.MallNotification;
import com.mt.messenger.domain.model.mall_notification.MallNotificationId;
import org.springframework.stereotype.Service;

@Service
public class MallNotificationService {
    public MallNotificationId create(SkuChangeFailed deserialize) {
        MallNotification mallNotification = new MallNotification(deserialize);
        DomainRegistry.getMallNotificationRepository().add(mallNotification);
        DomainRegistry.getUserNotificationService().notify(CommonDomainRegistry.getCustomObjectSerializer().serialize(new MallNotificationRepresentation(mallNotification)));
        return mallNotification.getMallNotificationId();
    }
}
