package com.hw.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hw.shared.ResourceServiceTokenHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class OAuthService {
    @Autowired
    RestTemplate restTemplate;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private ResourceServiceTokenHelper tokenHelper;

    @Value("${url.oauth}")
    private String url;

    /**
     * @return
     * @note admin account will receive notification
     */
    public String getAdminList() {
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> hashMapHttpEntity = new HttpEntity<>(headers);
        return tokenHelper.exchange(url, HttpMethod.GET, hashMapHttpEntity, String.class).getBody();
    }
}
