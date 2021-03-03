package com.mt.messenger.domain.model.mall_notification;

import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.audit.Auditable;
import com.mt.common.domain.model.idempotent.event.SkuChangeFailed;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

@Entity
@Table
@Getter
@NoArgsConstructor
public class MallNotification extends Auditable {
    @Id
    private Long id;
    private MallNotificationId mallNotificationId;
    private Long timestamp;
    @Lob
    private byte[] details;
    private MallNotificationType type;

    public MallNotification(SkuChangeFailed deserialize) {
        id = deserialize.getId();
        mallNotificationId = new MallNotificationId();
        timestamp = deserialize.getTimestamp();
        details = CommonDomainRegistry.getCustomObjectSerializer().nativeSerialize(deserialize.getChanges());
        type = MallNotificationType.SKU_FAILURE;
    }

}
