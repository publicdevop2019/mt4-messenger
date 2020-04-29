package com.hw.aggregate.message.model;

import com.hw.aggregate.message.exception.UnknownBizTypeException;
import com.hw.shared.Auditable;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;


@Entity
@Data
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"deliverTo", "bizType"}))
@SequenceGenerator(name = "messageId_gen", sequenceName = "messageId_gen", initialValue = 100)
public class Message extends Auditable {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "messageId_gen")
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

    public Message() {
    }

    public static Message create(String deliverTo, BizTypeEnum bizType) {
        return new Message(deliverTo, bizType);
    }

    public Message(String deliverTo, BizTypeEnum bizType) {
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
