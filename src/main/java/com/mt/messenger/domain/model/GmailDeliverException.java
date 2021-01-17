package com.mt.messenger.domain.model;

public class GmailDeliverException extends RuntimeException {
    public GmailDeliverException(Throwable cause) {
        super("error during gmail deliver", cause);
    }
}
