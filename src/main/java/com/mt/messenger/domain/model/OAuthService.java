package com.mt.messenger.domain.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mt.common.jwt.ResourceServiceTokenHelper;
import com.mt.common.service_discovery.EurekaRegistryHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class OAuthService {
    @Autowired
    RestTemplate restTemplate;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private ResourceServiceTokenHelper tokenHelper;

    @Value("${url.oauth}")
    private String url;

    @Autowired
    EurekaRegistryHelper eurekaRegistryHelper;

    /**
     * @return
     * @note admin account will receive notification
     */
    public String getAdminList() {
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> hashMapHttpEntity = new HttpEntity<>(headers);
        String body = tokenHelper.exchange(eurekaRegistryHelper.getProxyHomePageUrl() + url, HttpMethod.GET, hashMapHttpEntity, String.class).getBody();
        if (body != null && !body.equals("")) {
            String[] split = body.split(",");
            return split[0];
        } else {
            throw new NoAdminFoundException();
        }
    }
}
