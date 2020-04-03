package com.hw.controller;

import com.hw.service.PasswordResetService;
import com.hw.service.RegisterNotificationService;
import com.hw.service.ShopAdminNotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping( produces = "application/json")
@Slf4j
public class DeliverTaskController {

    @Autowired
    ShopAdminNotificationService shopAdminNotificationService;

    @Autowired
    RegisterNotificationService registerNotificationService;

    @Autowired
    PasswordResetService passwordResetService;

    @PostMapping("notifyBy/email/newOrder")
    public ResponseEntity<?> sendOrderInfoToAccount() {
        shopAdminNotificationService.saveDeliverRequest(null);
        return ResponseEntity.ok().build();
    }

    @PostMapping("notifyBy/email/activationCode")
    public ResponseEntity<?> sendActivationToUser(@RequestBody Map<String, String> map) {
        registerNotificationService.deliver(map);
        return ResponseEntity.ok().build();
    }

    @PostMapping("notifyBy/email/pwdReset")
    public ResponseEntity<?> sendPasswordResetToUser(@RequestBody Map<String, String> map) {
        passwordResetService.deliver(map);
        return ResponseEntity.ok().build();
    }
}


