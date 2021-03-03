package com.mt.messenger.application.mall_notification;

import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.domain_event.StoredEvent;
import com.mt.common.domain.model.domain_event.SubscribeForEvent;
import com.mt.common.domain.model.idempotent.event.SkuChangeFailed;
import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.common.domain.model.restful.query.PageConfig;
import com.mt.common.domain.model.restful.query.QueryConfig;
import com.mt.messenger.application.ApplicationServiceRegistry;
import com.mt.messenger.domain.DomainRegistry;
import com.mt.messenger.domain.model.mall_notification.MallNotification;
import com.mt.messenger.domain.model.system_notification.SystemNotification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MallNotificationApplicationService {
    @Transactional
    @SubscribeForEvent
    public void handleMonitorEvent(StoredEvent event) {
        ApplicationServiceRegistry.getIdempotentService().idempotent(null, event, event.getId().toString(), (command) -> {
            if (event.getName().equals(SkuChangeFailed.class.getName())) {
                SkuChangeFailed deserialize = CommonDomainRegistry.getCustomObjectSerializer().deserialize(event.getEventBody(), SkuChangeFailed.class);
                DomainRegistry.getMallNotificationService().create(deserialize);
            }
        }, SystemNotification.class);
    }

    public SumPagedRep<MallNotification> notificationsOf(String pageParam, String skipCount) {
        return DomainRegistry.getMallNotificationRepository().latestMallNotifications(new PageConfig(pageParam, 200), new QueryConfig(skipCount));
    }
}
