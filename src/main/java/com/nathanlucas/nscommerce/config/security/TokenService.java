package com.nathanlucas.nscommerce.config.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.nathanlucas.nscommerce.dtos.auth.TokenDTO;
import com.nathanlucas.nscommerce.entities.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class TokenService {
    @Value("${api.security.token.secret}")
    private String secret;

    public TokenDTO generateToken(User user){
        try{
            Algorithm algorithm = Algorithm.HMAC256(secret);
            Instant expirationDate = genExpirationDate();
            String accessToken = JWT.create()
                    .withIssuer("ns-commerce-api")
                    .withSubject(user.getEmail())
                    .withExpiresAt(expirationDate)
                    .sign(algorithm);
            TokenDTO token = new TokenDTO(accessToken,"bearer",toMilliseconds(expirationDate));
            return token;
        } catch (JWTCreationException exception) {
            throw new RuntimeException("Error while generating token", exception);
        }
    }

    public String validateToken(String token){
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.require(algorithm)
                    .withIssuer("ns-commerce-api")
                    .build()
                    .verify(token)
                    .getSubject();
        } catch (JWTVerificationException exception){
            return "";
        }
    }

    private Instant genExpirationDate(){
        return LocalDateTime.now().plusDays(1).toInstant(ZoneOffset.of("-03:00"));
    }

    private Long toMilliseconds(Instant moment) {
        Instant now = LocalDateTime.now().toInstant(ZoneOffset.of("-03:00"));
        return Duration.between(now, moment).toMillis()/1000;
    }
}