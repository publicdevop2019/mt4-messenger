package com.mt.messenger.port.adapter.web_socket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.Map;

//@Configuration
//@EnableWebSocketMessageBroker
public class SpringBootWebSocketConfig implements WebSocketMessageBrokerConfigurer {
    @Autowired
    private PrincipalHandshakeHandler principalHandshakeHandler;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/web-socket")
                .setAllowedOrigins("*")
                .setHandshakeHandler(principalHandshakeHandler)
                .withSockJS()
        ;
    }

    @Slf4j
    @Component
    public static class PrincipalHandshakeHandler extends DefaultHandshakeHandler {
        @Override
        protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler, Map<String, Object> attributes) {
            if (request instanceof ServletServerHttpRequest) {
                ServletServerHttpRequest servletServerHttpRequest = (ServletServerHttpRequest) request;
                HttpServletRequest httpRequest = servletServerHttpRequest.getServletRequest();
                final String token = httpRequest.getParameter("token");
                if (StringUtils.isEmpty(token)) {
                    return null;
                }
                return () -> token;
            }
            return null;
        }

    }
}
