package rs.ac.singidunum.rpg.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import rs.ac.singidunum.rpg.entity.User;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * Generisanje i validacija JWT tokena (HS256).
 * Pravi dva tipa tokena: kratkotrajni "access" i dugotrajni "refresh".
 */
@Service
public class JwtService {

    public static final String TYPE_CLAIM = "type";
    public static final String ACCESS = "access";
    public static final String REFRESH = "refresh";

    private final SecretKey key;
    private final long accessExpirationMs;
    private final long refreshExpirationMs;

    public JwtService(@Value("${app.jwt.secret}") String secret,
                      @Value("${app.jwt.access-expiration-ms}") long accessExpirationMs,
                      @Value("${app.jwt.refresh-expiration-ms}") long refreshExpirationMs) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessExpirationMs = accessExpirationMs;
        this.refreshExpirationMs = refreshExpirationMs;
    }

    public String generateAccessToken(User user) {
        return build(user, ACCESS, accessExpirationMs);
    }

    public String generateRefreshToken(User user) {
        return build(user, REFRESH, refreshExpirationMs);
    }

    private String build(User user, String type, long ttlMs) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + ttlMs);
        return Jwts.builder()
                .subject(user.getUsername())
                .claim("role", user.getRole().name())
                .claim(TYPE_CLAIM, type)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(key)
                .compact();
    }

    /** Validira potpis i rok; baca JwtException ako token nije ispravan. */
    public Claims parse(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String extractUsername(String token) {
        return parse(token).getSubject();
    }

    public boolean isTokenType(String token, String expectedType) {
        return expectedType.equals(parse(token).get(TYPE_CLAIM, String.class));
    }
}
