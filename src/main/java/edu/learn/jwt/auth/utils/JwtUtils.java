package edu.learn.jwt.auth.utils;

import edu.learn.jwt.exceptions.ExpirationDateNotInTokenException;
import edu.learn.jwt.exceptions.TokenExpired;
import edu.learn.jwt.exceptions.UserInTokenInvalid;
import edu.learn.jwt.exceptions.UserNotInTokenException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.vavr.control.Option;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static java.time.ZonedDateTime.now;

@Component
public class JwtUtils {

    private final String SECRET = "HIDE THIS!";

    public Option<String> getUserFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject)
                .onEmpty(() -> {
                    throw new UserNotInTokenException();
                });
    }

    private Option<Date> getExpirationFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration)
                .onEmpty(() -> {
                    throw new ExpirationDateNotInTokenException();
                });
    }

    private <T> Option<T> getClaimFromToken(String token, Function<Claims, T> resolver) {
        return Option.of(resolver.apply(getAllClaimsFromToken(token)));
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET)
                .parseClaimsJws(token)
                .getBody();
    }

    public Option<Boolean> validateToken(String token, String username) {
        return getUserFromToken(token)
                .filter(u -> u.equals(username))
                .onEmpty(() -> {
                    throw new UserInTokenInvalid();
                })
                .map(u -> tokenIsNotExpired(token).get());
    }

    private Option<Boolean> tokenIsNotExpired(String token) {
        return tokenIsExpired(token)
                .map(isExpired -> {
                    if (isExpired)
                        throw new TokenExpired();
                    return true;
                });
    }

    private Option<Boolean> tokenIsExpired(String token) {
        return getExpirationFromToken(token)
                .map(exp -> exp.before(Date.from(now().toInstant())));
    }

    public String generateToken(String username) {
        return generateToken(new HashMap<>(), username);
    }

    public String refreshToken(String token, String username) {
        Claims claims = getAllClaimsFromToken(token);
        claims.setExpiration(Date.from(now().plusWeeks(1).toInstant()));

        return generateToken(claims, username);
    }

    private String generateToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(Date.from(now().toInstant()))
                .setExpiration(Date.from(now().plusWeeks(1).toInstant()))
                    .signWith(SignatureAlgorithm.HS512, SECRET).compact();
    }
}
