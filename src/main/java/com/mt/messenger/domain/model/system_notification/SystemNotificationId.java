package com.mt.messenger.domain.model.system_notification;

import com.mt.common.domain.model.CommonDomainRegistry;
import com.mt.common.domain.model.domainId.DomainId;

public class SystemNotificationId extends DomainId {
    public SystemNotificationId() {
        super();
        Long id = CommonDomainRegistry.uniqueIdGeneratorService().id();
        String s = Long.toString(id, 36);
        setDomainId("4S" + s.toUpperCase());
    }

    public SystemNotificationId(String domainId) {
        super(domainId);
    }
}
