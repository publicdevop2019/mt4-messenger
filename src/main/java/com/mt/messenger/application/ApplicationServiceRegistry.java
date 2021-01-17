package com.mt.messenger.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ApplicationServiceRegistry {
    @Autowired
    public void setRevokeTokenApplicationService(MessageApplicationService revokeTokenApplicationService) {
        ApplicationServiceRegistry.messageApplicationService = revokeTokenApplicationService;
    }

    private static MessageApplicationService messageApplicationService;

    public static MessageApplicationService messageApplicationService() {
        return messageApplicationService;
    }
}
