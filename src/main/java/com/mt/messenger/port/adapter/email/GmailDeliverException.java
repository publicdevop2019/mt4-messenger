package com.mt.messenger.port.adapter.email;

public class GmailDeliverException extends RuntimeException {
    public GmailDeliverException(Throwable cause) {
        super("error during gmail deliver", cause);
    }
}
