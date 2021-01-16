package com.mt.messenger.model;

public class GmailDeliverException extends RuntimeException {
    public GmailDeliverException(Throwable cause) {
        super("error during gmail deliver", cause);
    }
}
