package com.tribly.service.auth;

import com.tribly.domain.user.User;
import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.time.Duration;
import java.time.Instant;
import java.util.Set;

@ApplicationScoped
public class JwtService {

    @ConfigProperty(name = "mp.jwt.verify.issuer")
    String issuer;

    @ConfigProperty(name = "smallrye.jwt.new-token.lifespan", defaultValue = "86400")
    long tokenLifespanSeconds;

    public String generateToken(User user) {
        return generateToken(user, Duration.ofSeconds(tokenLifespanSeconds));
    }

    public String generateToken(User user, Duration validity) {
        Instant now = Instant.now();
        Instant expiry = now.plus(validity);

        var builder = Jwt.issuer(issuer)
                .subject(String.valueOf(user.getId()))
                .upn(user.getEmail())
                .claim("name", user.getDisplayName())
                .claim("email", user.getEmail())
                .groups(Set.of("user"))
                .issuedAt(now)
                .expiresAt(expiry);

        // Only add avatar claim if not null
        if (user.getAvatarUrl() != null) {
            builder.claim("avatar", user.getAvatarUrl());
        }

        return builder.sign();
    }

    public String generateRefreshToken(User user) {
        Instant now = Instant.now();
        Instant expiry = now.plus(Duration.ofDays(30));

        return Jwt.issuer(issuer)
                .subject(String.valueOf(user.getId()))
                .upn(user.getEmail())
                .claim("type", "refresh")
                .groups(Set.of("refresh"))
                .issuedAt(now)
                .expiresAt(expiry)
                .sign();
    }

    public TokenPair generateTokenPair(User user) {
        return new TokenPair(
                generateToken(user),
                generateRefreshToken(user),
                tokenLifespanSeconds
        );
    }

    public record TokenPair(
            String accessToken,
            String refreshToken,
            long expiresIn
    ) {}
}
