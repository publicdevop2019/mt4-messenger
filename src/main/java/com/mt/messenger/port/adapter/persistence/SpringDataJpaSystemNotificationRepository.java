package com.mt.messenger.port.adapter.persistence;

import com.mt.common.persistence.QueryConfig;
import com.mt.common.query.DefaultPaging;
import com.mt.common.sql.SumPagedRep;
import com.mt.messenger.domain.model.system_notification.SystemNotification;
import com.mt.messenger.domain.model.system_notification.SystemNotificationRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpringDataJpaSystemNotificationRepository extends JpaRepository<SystemNotification, Long>, SystemNotificationRepository {
    Page<SystemNotification> findAll(Pageable pageable);

    default void add(SystemNotification notification) {
        save(notification);
    }

    default SumPagedRep<SystemNotification> latestSystemNotifications(DefaultPaging defaultPaging, QueryConfig queryConfig) {
        Pageable sortedByTimestampDesc =
                PageRequest.of((int) defaultPaging.getPageNumber(), defaultPaging.getPageSize(), Sort.by("timestamp").descending());
        Page<SystemNotification> all = findAll(sortedByTimestampDesc);
        SumPagedRep<SystemNotification> systemNotificationSumPagedRep = new SumPagedRep<>();
        if (!all.getContent().isEmpty())
            systemNotificationSumPagedRep.setData(all.getContent());
        systemNotificationSumPagedRep.setTotalItemCount(all.getTotalElements());
        return systemNotificationSumPagedRep;
    }
}
