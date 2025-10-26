package ru.mephi.bookingservice.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class TokenService {

    @Value("${app.jwt.secret}")
    private String secret;

    @Value("${app.jwt.token-expiration-milliseconds}")
    private Long tokenExpirationMills;

    public String createToken(String subject, String scope) {
        HashMap<String, Object> tokenAttributes = new HashMap<>();
        tokenAttributes.put("scope", scope);

        long currentSeconds = System.currentTimeMillis();
        Claims finalClaims = Jwts.claims(tokenAttributes);
        finalClaims.setSubject(subject);

        return Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setClaims(finalClaims)
                .setIssuedAt(new Date(currentSeconds))
                .setExpiration(new Date(currentSeconds + tokenExpirationMills))
                .signWith(Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)), SignatureAlgorithm.HS256)
                .compact();
    }
}
