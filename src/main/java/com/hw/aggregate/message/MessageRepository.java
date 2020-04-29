package com.hw.aggregate.message;

import com.hw.aggregate.message.model.BizTypeEnum;
import com.hw.aggregate.message.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import javax.persistence.LockModeType;
import java.util.Optional;

/**
 * use pessimistic lock to prevent code from executing, due to third party api can not be undo
 */
public interface MessageRepository extends JpaRepository<Message, Long> {
    @Lock(LockModeType.PESSIMISTIC_FORCE_INCREMENT)
    @Query("SELECT p FROM #{#entityName} as p WHERE p.deliverTo = ?1 AND p.bizType = ?2")
    Optional<Message> findByDeliverToAndBizType(String deliverTo, BizTypeEnum bizTypeEnum);
}
