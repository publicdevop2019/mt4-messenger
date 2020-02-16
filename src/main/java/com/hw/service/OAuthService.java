package com.hw.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hw.shared.ResourceServiceTokenHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
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
        /**
         * get jwt token
         */
        if (tokenHelper.storedJwtToken == null)
            tokenHelper.storedJwtToken = tokenHelper.getJwtToken();
        headers.setBearerAuth(tokenHelper.storedJwtToken);
        HttpEntity<String> hashMapHttpEntity = new HttpEntity<>(null, headers);
        ResponseEntity<String> exchange;
        try {
            exchange = restTemplate.exchange(url, HttpMethod.GET, hashMapHttpEntity, String.class);
        } catch (HttpClientErrorException ex) {
            /**
             * re-try if jwt expires
             */
            tokenHelper.storedJwtToken = tokenHelper.getJwtToken();
            headers.setBearerAuth(tokenHelper.storedJwtToken);
            HttpEntity<String> hashMapHttpEntity2 = new HttpEntity<>(null, headers);
            exchange = restTemplate.exchange(url, HttpMethod.GET, hashMapHttpEntity2, String.class);
        }
        return exchange.getBody();
    }
}
