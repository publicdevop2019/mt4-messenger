package com.mt.messenger.domain.model.email_delivery;

import com.mt.common.audit.Auditable;
import com.mt.messenger.application.email_delivery.UnknownBizTypeException;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;


@Entity
@Data
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"deliverTo", "bizType"}))
public class EmailDelivery extends Auditable {
    @Id
    private Long id;

    @Column
    private String deliverTo;

    @Column
    private BizTypeEnum bizType;

    @Column
    private Boolean lastTimeResult;

    @Column
    private Date lastSuccessTime;

    @Version
    private Integer version;

    public EmailDelivery() {
    }

    public static EmailDelivery create(Long id, String deliverTo, BizTypeEnum bizType) {
        return new EmailDelivery(id, deliverTo, bizType);
    }

    public EmailDelivery(Long id, String deliverTo, BizTypeEnum bizType) {
        this.id = id;
        this.deliverTo = deliverTo;
        this.bizType = bizType;
        this.lastTimeResult = false;
        this.lastSuccessTime = null;
    }

    public Boolean hasCoolDown() {
        if (lastSuccessTime == null)
            return true;
        if (bizType.equals(BizTypeEnum.NEW_ORDER)) {
            return System.currentTimeMillis() > lastSuccessTime.getTime() + 300 * 1000;
        } else if (bizType.equals(BizTypeEnum.PWD_RESET)) {
            return System.currentTimeMillis() > lastSuccessTime.getTime() + 60 * 1000;
        } else if (bizType.equals(BizTypeEnum.NEW_USER_CODE)) {
            return System.currentTimeMillis() > lastSuccessTime.getTime() + 60 * 1000;
        } else {
            throw new UnknownBizTypeException();
        }
    }

    public void onMsgSendSuccess() {
        lastTimeResult = Boolean.TRUE;
        lastSuccessTime = new Date(System.currentTimeMillis());
    }
}
