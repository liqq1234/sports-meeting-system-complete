package com.sports.gateway.filter;

import com.sports.gateway.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
@Slf4j
public class AuthGlobalFilter implements GlobalFilter, Ordered {

    @Autowired
    private JwtProperties jwtProperties;

    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getPath().toString();

        // 1. 白名单检查
        List<String> whiteList = jwtProperties.getWhiteList();
        if (whiteList != null) {
            for (String pattern : whiteList) {
                if (pathMatcher.match(pattern, path)) {
                    return chain.filter(exchange);
                }
            }
        }

        // 2. 获取令牌
        String token = request.getHeaders().getFirst("Authorization");
        if (token == null || token.isEmpty()) {
            return unauth(exchange.getResponse(), "Missing token");
        }

        // 处理 Bearer 格式
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        // 3. 校验令牌
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8))
                    .parseClaimsJws(token)
                    .getBody();

            // 令牌合法，提取身份信息注入 Header 下传给业务服服务
            String username = claims.getSubject();
            Object userId = claims.get("userId");
            Object role = claims.get("role");
            Object gender = claims.get("gender");
            log.info("Request user: {}, userId: {}, path: {}", username, userId, path);

            ServerHttpRequest builtRequest = request.mutate()
                    .header("X-User-ID", userId != null ? String.valueOf(userId) : "")
                    .header("X-User-Name", username)
                    .header("X-User-Role", role != null ? String.valueOf(role) : "")
                    .header("X-User-Gender", gender != null ? String.valueOf(gender) : "")
                    .build();

            return chain.filter(exchange.mutate().request(builtRequest).build());
        } catch (Exception e) {
            log.error("Token validation failed: {}", e.getMessage());
            return unauth(exchange.getResponse(), "Invalid token");
        }
    }

    private Mono<Void> unauth(ServerHttpResponse response, String message) {
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
        String body = "{\"code\": 401, \"msg\": \"" + message + "\"}";
        DataBuffer buffer = response.bufferFactory().wrap(body.getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(buffer));
    }

    @Override
    public int getOrder() {
        return -100;
    }
}
