package com.mt.messenger.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mt.common.domain_event.DomainEvent;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserPwdResetCodeUpdated {
    private String email;
    private String code;
}
