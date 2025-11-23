package com.devteria.chat.controller;

import java.time.Instant;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

import org.springframework.stereotype.Component;

import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.annotation.OnConnect;
import com.corundumstudio.socketio.annotation.OnDisconnect;
import com.devteria.chat.dto.request.IntrospectRequest;
import com.devteria.chat.entity.WebSocketSession;
import com.devteria.chat.service.IdentityService;
import com.devteria.chat.service.WebSocketSessionService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class SocketHandler {
    SocketIOServer server;
    IdentityService identityService;
    WebSocketSessionService webSocketSessionService;

    @OnConnect
    public void clientConnected(SocketIOClient client) {
        // Get token from request param
        String token = client.getHandshakeData().getSingleUrlParam("token");
        // Verify token
        var introspectResponse = identityService.introspect(
                IntrospectRequest.builder().token(token).build());
        // If token ís invalid disconnect
        if (introspectResponse.isValid()) {
            log.error("Authentication connected :{}", client.getSessionId());
            // Persist webSocketSession
            WebSocketSession webSocketSession = WebSocketSession.builder()
                    .socketSessionId(client.getSessionId().toString())
                    .userId(introspectResponse.getUserId())
                    .createdAt(Instant.now())
                    .build();
            webSocketSessionService.create(webSocketSession);
            log.info("Web Socket Session Id ,{}", webSocketSession);
        } else {
            log.error("Authentication faild :{}", client.getSessionId());
            client.disconnect();
            ;
        }
        //

        log.info("Client connected : {},{}", client.getSessionId(), token);
    }

    @OnDisconnect
    public void clientDisConnected(SocketIOClient client) {
        log.info("Client disConnected : {}", client.getSessionId());
        webSocketSessionService.deleteSession(client.getSessionId().toString());
    }

    @PostConstruct
    public void startServer() {
        server.start();
        server.addListeners(this);
        log.info("Socket server started");
    }

    @PreDestroy
    public void stopServer() {
        server.stop();
        log.info("Socket server stoped");
    }
}
