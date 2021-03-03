package com.mt.messenger.domain.model.mall_notification;

public enum MallNotificationType {
    SKU_FAILURE(10001);
    public final Integer errorCode;

    MallNotificationType(int i) {
        this.errorCode = i;
    }
}
