package com.hw.service;

import java.util.Map;

public interface SaveThenScheduleDeliverService {
    void deliver();

    void saveDeliverRequest(Map<String, String> map);
}
