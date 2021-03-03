package com.mt.messenger.application.mall_notification;

import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.restful.PatchCommand;
import com.mt.messenger.domain.model.mall_notification.MallNotification;
import com.mt.messenger.domain.model.mall_notification.MallNotificationType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Getter
public class MallNotificationRepresentation implements Serializable {
    private Long date;
    private MallNotificationType type;
    private Integer code;
    private final List<SkuChange> skuChange = new ArrayList<>();

    public MallNotificationRepresentation(Object o) {
        MallNotification notification = (MallNotification) o;
        date = notification.getTimestamp();
        type = notification.getType();
        code = notification.getType().errorCode;
        if (type.equals(MallNotificationType.SKU_FAILURE)) {
            List<PatchCommand> commands = (List<PatchCommand>) CommonDomainRegistry.getCustomObjectSerializer().nativeDeserialize(notification.getDetails());
            commands.forEach(e -> {
                String skuId = e.getPath().split("/")[1];
                String s = e.getPath().split("/")[2];
                if (s.equalsIgnoreCase("storageActual")) {
                    skuChange.add(new SkuChange(skuId, StorageType.ACTUAL, (Integer) e.getValue()));
                } else {
                    if (s.equalsIgnoreCase("storageOrder")) {
                        skuChange.add(new SkuChange(skuId, StorageType.ORDER, (Integer) e.getValue()));
                    } else {
                        throw new IllegalArgumentException("unknown storage type: " + s);
                    }
                }
            });
        }
    }

    public enum StorageType implements Serializable {
        ORDER,
        ACTUAL;
    }

    @AllArgsConstructor
    @Data
    private static class SkuChange implements Serializable {
        private String skuId;
        private StorageType storageType;
        private Integer amount;
    }
}
