package com.hw.service;

import com.hw.entity.DeliverTask;
import com.hw.repo.DeliverTaskRepo;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MergeSimilarDeliverService implements SaveThenScheduleDeliverService {
    @Autowired
    DeliverTaskRepo deliverTaskRepo;

    public static String type;

    public static Long debounceTime;

    public List<DeliverTask> scanPendingDeliverTask(Long debounceTime, String type) {

        Supplier<Stream<DeliverTask>> streamSupplier = () -> deliverTaskRepo.findAll().stream().filter(DeliverTask::isPending);

        List<String> pendingDeliverTos = streamSupplier.get().map(DeliverTask::getDeliverTo).distinct().collect(Collectors.toList());

        Stream<Optional<DeliverTask>> optionalStream = pendingDeliverTos.stream().map(deliverTo -> findEarliestDeliverTask(streamSupplier, type, deliverTo));

        Stream<DeliverTask> optionalStream1 = optionalStream.filter(Optional::isPresent).map(Optional::get).peek(earliestDeliverTask -> {
            List<DeliverTask> collect = streamSupplier.get().filter(e -> e.isReady(debounceTime, earliestDeliverTask) && e.getDeliverTo().equals(earliestDeliverTask.getDeliverTo())).collect(Collectors.toList());
            // merge multiple deliver task to one
            earliestDeliverTask.merge(collect, deliverTaskRepo);
        });
        return optionalStream1.collect(Collectors.toList());
    }

    private Optional<DeliverTask> findEarliestDeliverTask(Supplier<Stream<DeliverTask>> streamSupplier, String type, String deliverTo) {
        return streamSupplier.get().filter(e -> e.getDeliverTo().equals(deliverTo) && e.getType().equalsIgnoreCase(type)).min(Comparator.comparingLong(e -> e.getCreatedAt().toInstant().toEpochMilli()));
    }

    @Override
    public void deliver() {

    }

    @Override
    public void saveDeliverRequest(Map<String, String> map) {

    }

}
