package com.example.springjava.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Optional;

@Slf4j
@Component
public class JwtHelper {
    private final String secret = "secret";
    private Algorithm algorithm;
    private JWTVerifier verifier;

    private int tokenDuration;
    private Date tokenExpiresAt;

    public JwtHelper() {
        this.tokenDuration = 60 * 60 * 1000;
        this.tokenExpiresAt = new Date(System.currentTimeMillis() + this.tokenDuration);

        this.algorithm = Algorithm.HMAC256(this.secret);
        this.verifier  = JWT.require(this.algorithm).build();
    }

    public int getTokenDuration() {
        return this.tokenDuration;
    }

    public String getExpireDate() {
        return this.tokenExpiresAt.toString();
    }

    public String generateAccessToken(String subject) {

        String access_token = JWT.create()
                .withSubject(subject)
                .withExpiresAt(this.tokenExpiresAt)
                .sign(this.algorithm);

        return access_token;
    }

    private Optional<DecodedJWT> decodeAccessToken(String token) {
        try {
            return Optional.of(this.verifier.verify(token));
        } catch (JWTVerificationException e) {
            log.error("invalid access token", e);
        }
        return Optional.empty();
    }

    public String getUsernameFromAccessToken(String token) {
        return decodeAccessToken(token).get().getSubject();
    }
}

