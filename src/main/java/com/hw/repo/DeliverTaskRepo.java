package com.hw.repo;

import com.hw.entity.DeliverTask;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeliverTaskRepo extends JpaRepository<DeliverTask, Long> {
}
