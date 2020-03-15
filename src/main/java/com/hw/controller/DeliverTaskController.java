package com.hw.controller;

import com.hw.service.ShopAdminNotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "v1/api", produces = "application/json")
@Slf4j
public class DeliverTaskController {

    @Autowired
    ShopAdminNotificationService shopAdminNotificationService;

    @PostMapping("notifyBy/email/newOrder")
    public ResponseEntity<?> sendOrderInfoToAccountViaEmail() {
        shopAdminNotificationService.saveDeliverRequest();
        return ResponseEntity.ok().build();
    }
}


