package fr.tobby.tripnjoyback.auth;

import fr.tobby.tripnjoyback.exception.auth.TokenExpiredException;
import fr.tobby.tripnjoyback.exception.auth.TokenParsingException;
import fr.tobby.tripnjoyback.exception.auth.TokenVerificationException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;

@Component
public final class TokenManager {

    private final String jwtSecret;
    private final UserDetailsService userDetailsService;

    public TokenManager(@Value("${jwt.secret}") final String jwtSecret,
                        final UserDetailsService userDetailsService)
    {
        this.jwtSecret = jwtSecret;
        this.userDetailsService = userDetailsService;
    }

    public String generateFor(UserDetails userDetails, final long userId)
    {
        return generateFor(userDetails.getUsername(), userId);
    }

    public String generateFor(String username, final long userId)
    {
        final HashMap<String, Object> claims = new HashMap<>();

        claims.put("userId", userId);
        return Jwts.builder()
                   .setClaims(claims)
                   .setExpiration(Date.from(Instant.now().plus(10, ChronoUnit.DAYS)))
                   .setIssuedAt(Date.from(Instant.now()))
                   .setSubject(username)
                   .signWith(SignatureAlgorithm.HS256, jwtSecret)
                   .compact();
    }

    public UserDetails verifyToken(final String tokenString) throws TokenVerificationException
    {
        Claims claims;
        try
        {
            claims = Jwts.parser()
                         .setSigningKey(jwtSecret)
                         .parseClaimsJws(tokenString)
                         .getBody();
        }
        catch (Exception e)
        {
            throw new TokenParsingException(e);
        }

        if (claims.getExpiration().before(Date.from(Instant.now())))
            throw new TokenExpiredException();
        String username = claims.getSubject();
        return userDetailsService.loadUserByUsername(username);
    }
}
