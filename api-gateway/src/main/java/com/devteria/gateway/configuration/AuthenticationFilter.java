package com.devteria.gateway.configuration;

import com.devteria.gateway.dto.ApiResponse;
import com.devteria.gateway.service.IdentityService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.netty.http.server.HttpServer;
import reactor.netty.http.server.HttpServerResponse;

import java.util.Arrays;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class AuthenticationFilter implements GlobalFilter, Ordered {
    IdentityService identityService;
    ObjectMapper objectMapper;
    @NonFinal
    private String[]publicEndpoints = {"/identity/auth/.*","/identity/auth/token","/identity/auth/introspect","/identity/auth/logout","/identity/users/registration"
    ,"/file/media/download/"};


    @Value("${app.api-prefix}")
    @NonFinal
    private String apiPrefix;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        log.info("enter authentication filter.....");
        System.out.println(isPublicEndpoint(exchange.getRequest()));

        if (isPublicEndpoint(exchange.getRequest()))
            return chain.filter(exchange);


        // Get token from authorization header
       List<String>authHeader = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION);
        if (CollectionUtils.isEmpty(authHeader))
            return unauthenticated(exchange.getResponse());

        String token = authHeader.getFirst();
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        log.info("Token : {}",token);
        // verify token
        return identityService.introspect(token).flatMap(introspectResponseApiResponse -> {
            if(introspectResponseApiResponse.getResult().isValid())
                return chain.filter(exchange);
            else
                return unauthenticated(exchange.getResponse());
        }).onErrorResume(throwable -> unauthenticated((exchange.getResponse())));


    }

    private boolean isPublicEndpoint(org.springframework.http.server.reactive.ServerHttpRequest request) {


        return Arrays.stream(publicEndpoints)
                .anyMatch(endpoint -> request.getURI().getPath().startsWith(apiPrefix + endpoint));
    }

    @Override
    public int getOrder() {
        return -1;
    }



    Mono<Void>unauthenticated(ServerHttpResponse response) {
        ApiResponse<?>apiResponse = ApiResponse.builder()
                .code(1401)
                .message("Unauthenticated")

                .build();
    String body = null;


        try {
            body = objectMapper.writeValueAsString(apiResponse);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        response.setStatusCode(HttpStatus.UNAUTHORIZED);

        response.getHeaders().add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

    return response.writeWith(
            Mono.just(response.bufferFactory().wrap(body.getBytes()))
    );
    }
}
