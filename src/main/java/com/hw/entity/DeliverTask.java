package com.hw.entity;

import com.hw.clazz.MapConverter;
import com.hw.clazz.StringListConverter;
import com.hw.repo.DeliverTaskRepo;
import com.hw.shared.Auditable;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Entity
@Data
@Table
@SequenceGenerator(name = "deliverId_gen", sequenceName = "deliverId_gen", initialValue = 100)
public class DeliverTask extends Auditable {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "deliverId_gen")
    private Long id;

    @Column
    private String deliverTo;

    @Column
    private String type;

    @Column
    @Convert(converter = MapConverter.class)
    private Map<String, String> templateParams;

    @Column
    private Integer mergeCount = 0;

    @Column
    private String mergeTo;

    @Column
    private Boolean merged = Boolean.FALSE;

    @Column
    @Convert(converter = StringListConverter.class)
    private List<String> mergeFrom;

    @Column
    private Boolean carryOver = Boolean.FALSE;

    @Column
    private Boolean deliverStatus = Boolean.FALSE;

    @Column
    private Integer failedCount = 0;

    @Column
    @Convert(converter = StringListConverter.class)
    private List<String> errorMsg;

    @Autowired
    @Transient
    DeliverTaskRepo deliverTaskRepo;

    public static DeliverTask create(String deliverTo, String type) {
        DeliverTask deliverTask = new DeliverTask();
        deliverTask.setDeliverTo(deliverTo);
        deliverTask.setType(type);
        return deliverTask;
    }

    public void merge(List<DeliverTask> deliverTasks) {
        mergeCount = deliverTasks.size();
        mergeFrom = deliverTasks.stream().map(e -> e.getId().toString()).collect(Collectors.toList());
        deliverTasks.forEach(e -> {
            e.mergeTo = mergeTo;
            e.merged = Boolean.TRUE;
            deliverTaskRepo.save(e);
        });
        deliverTaskRepo.save(this);
    }

    public Boolean isReady(Long debounceTime, DeliverTask earliestDeliverTask) {
        // exclude earliestDeliverTask
        if (id.equals(earliestDeliverTask.id) || !isPending())
            return false;
        long l = earliestDeliverTask.getCreatedAt().toInstant().toEpochMilli() + debounceTime;
        Instant instant = Instant.ofEpochMilli(l);
        return getCreatedAt().compareTo(Date.from(instant)) <= 0;
    }

    public Boolean isPending() {
        return !deliverStatus && !merged;
    }

    public void onMsgDeliverFailure(Long debounceTime, String errorMsg) {
        setCreatedAt(Date.from(getCreatedAt().toInstant().plusMillis(debounceTime)));
        deliverStatus = Boolean.FALSE;
        carryOver = Boolean.TRUE;
        if (this.errorMsg == null)
            this.errorMsg = new ArrayList<>();
        this.errorMsg.add(errorMsg);
        failedCount++;
        deliverTaskRepo.save(this);
    }

    public void onMsgDeliverSuccess() {
        deliverStatus = Boolean.TRUE;
        deliverTaskRepo.save(this);
    }

}
