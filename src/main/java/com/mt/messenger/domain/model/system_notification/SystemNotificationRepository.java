package com.mt.messenger.domain.model.system_notification;

import com.mt.common.persistence.QueryConfig;
import com.mt.common.query.DefaultPaging;
import com.mt.common.sql.SumPagedRep;

public interface SystemNotificationRepository {
    void add(SystemNotification notification);

    SumPagedRep<SystemNotification> latestSystemNotifications(DefaultPaging defaultPaging, QueryConfig queryConfig);
}
