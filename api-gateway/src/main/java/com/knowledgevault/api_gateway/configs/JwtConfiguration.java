package com.knowledgevault.api_gateway.configs;

import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import javax.crypto.SecretKey;

@Configuration
public class JwtConfiguration {
    private final JwtConfigurationProperties properties;

    JwtConfiguration(JwtConfigurationProperties properties) {
        this.properties = properties;
    }

    @Bean
    ReactiveJwtDecoder jwtDecoder() {
        byte[] keyBytes = Decoders.BASE64.decode(properties.getSecretKey());

        SecretKey key = Keys.hmacShaKeyFor(keyBytes);

        return NimbusReactiveJwtDecoder
                .withSecretKey(key)
                .build();
    }
}
