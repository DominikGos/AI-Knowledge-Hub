package com.knowledgevault.api_gateway.configs;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "jwt")
public class JwtConfiguration {

    private String secretKey;

    private long expirationTime;
}
