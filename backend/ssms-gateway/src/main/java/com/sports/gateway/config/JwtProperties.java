package com.sports.gateway.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * JWT Properties configuration
 */
@Data
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {
    /**
     * JWT secret key
     */
    private String secret;

    /**
     * Whitelist paths that don't require authentication
     */
    private List<String> whiteList = new ArrayList<>();
}
