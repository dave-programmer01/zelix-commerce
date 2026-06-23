package com.heraim.zelix.common.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "zelix.jwt")
@Component
@Getter @Setter
public class JwtProperties {
    private String secret;
    private long accessTokenExpiryMinutes;
    private long refreshTokenExpiryDays;
}
