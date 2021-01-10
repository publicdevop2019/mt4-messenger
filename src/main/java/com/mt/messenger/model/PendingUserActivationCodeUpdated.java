package com.mt.messenger.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class PendingUserActivationCodeUpdated {
    private String email;
    private String code;
}