package com.hw.controller;

import com.hw.service.EmailServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "v1/api", produces = "application/json")
@Slf4j
public class EmailController {
    @Autowired
    EmailServiceImpl emailService;

    @PostMapping("notifyBy/email/newOrder")
    public ResponseEntity<?> sendOrderInfoToAccountViaEmail() {
        emailService.sendOrderInfoToAccountViaEmail();
        return ResponseEntity.ok().build();
    }
}


