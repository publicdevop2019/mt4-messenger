package com.mt.messenger.domain.model.mall_notification;

import com.mt.common.domain.model.audit.Auditable;
import com.mt.common.domain.model.idempotent.event.SkuChangeFailed;
import com.mt.common.domain.model.restful.PatchCommand;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;
import java.util.stream.Collectors;

@Entity
@Table
@Getter
@NoArgsConstructor
public class MallNotification extends Auditable {
    @Id
    private Long id;
    private MallNotificationId mallNotificationId;
    private Long timestamp;
    private String details;

    public MallNotification(SkuChangeFailed deserialize) {
        id = deserialize.getId();
        mallNotificationId = new MallNotificationId();
        timestamp = deserialize.getTimestamp();
        details = "sku change failed with path: " + deserialize.getChanges().stream().map(PatchCommand::getPath).collect(Collectors.joining(","));
    }
}
