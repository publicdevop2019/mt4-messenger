package com.mt.messenger.resource;

import com.mt.messenger.application.MessageApplicationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(produces = "application/json")
public class MessageResource {

    @Autowired
    private MessageApplicationService messageApplicationService;

    @PostMapping("notifyBy/email/newOrder")
    public ResponseEntity<?> sendOrderInfoToAccount() {
        messageApplicationService.sendNewOrderEmail();
        return ResponseEntity.ok().build();
    }
}


