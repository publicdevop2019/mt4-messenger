package com.mt.messenger.application.system_notification;

import com.mt.common.domain.model.CommonDomainRegistry;
import com.mt.common.domain_event.StoredEvent;
import com.mt.common.domain_event.SubscribeForEvent;
import com.mt.common.idempotent.HangingTxDetected;
import com.mt.common.persistence.QueryConfig;
import com.mt.common.query.DefaultPaging;
import com.mt.common.sql.SumPagedRep;
import com.mt.messenger.application.ApplicationServiceRegistry;
import com.mt.messenger.domain.DomainRegistry;
import com.mt.messenger.domain.model.system_notification.SystemNotification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SystemNotificationApplicationService {
    @Transactional
    @SubscribeForEvent
    public void handleMonitorEvent(StoredEvent event) {
        ApplicationServiceRegistry.idempotentWrapper().idempotent(event, event.getId().toString(), (command) -> {
            if (event.getName().equals(HangingTxDetected.class.getName())) {
                HangingTxDetected deserialize = CommonDomainRegistry.customObjectSerializer().deserialize(event.getEventBody(), HangingTxDetected.class);
                DomainRegistry.systemNotificationService().create(deserialize);
            }
        }, SystemNotification.class);
    }

    public SumPagedRep<SystemNotification> notificationsOf(String pageParam, String skipCount) {
        return DomainRegistry.systemNotificationRepository().latestSystemNotifications(new DefaultPaging(pageParam), new QueryConfig(skipCount));
    }
}
