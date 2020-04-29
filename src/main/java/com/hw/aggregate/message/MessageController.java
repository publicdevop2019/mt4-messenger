package com.hw.aggregate.message;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping(produces = "application/json")
@Slf4j
public class MessageController {

    @Autowired
    private MessageApplicationService messageApplicationService;

    @PostMapping("notifyBy/email/newOrder")
    public ResponseEntity<?> sendOrderInfoToAccount() {
        messageApplicationService.sendNewOrderEmail();
        return ResponseEntity.ok().build();
    }

    @PostMapping("notifyBy/email/activationCode")
    public ResponseEntity<?> sendActivationToUser(@RequestBody Map<String, String> map) {
        messageApplicationService.sendActivationCodeEmail(map);
        return ResponseEntity.ok().build();
    }

    @PostMapping("notifyBy/email/pwdReset")
    public ResponseEntity<?> sendPasswordResetToUser(@RequestBody Map<String, String> map) {
        messageApplicationService.sendPwdResetEmail(map);
        return ResponseEntity.ok().build();
    }
}


