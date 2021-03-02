package com.mt.messenger.application.mall_notification;

import com.mt.messenger.domain.model.system_notification.SystemNotification;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class MallNotificationRepresentation {
    private Long date;
    private String message;

    public MallNotificationRepresentation(Object o) {
        SystemNotification notification = (SystemNotification) o;
        date = notification.getTimestamp();
        message = notification.getDetails();
    }
}
