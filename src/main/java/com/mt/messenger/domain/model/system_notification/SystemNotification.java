package com.mt.messenger.domain.model.system_notification;

import com.mt.common.audit.Auditable;
import com.mt.common.idempotent.HangingTxDetected;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;

@Entity
@Table
@Getter
@NoArgsConstructor
public class SystemNotification extends Auditable {
    @Id
    private Long id;
    @Version
    private Integer version;
    private SystemNotificationId systemNotificationId;
    private Long timestamp;
    private String details;

    public SystemNotification(HangingTxDetected deserialize) {
        id = deserialize.getId();
        systemNotificationId = new SystemNotificationId();
        timestamp = deserialize.getTimestamp();
        details = "Hanging transaction detected with change id " + deserialize.getChangeId();
    }
}
