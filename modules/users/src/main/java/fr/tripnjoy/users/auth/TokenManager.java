package fr.tripnjoy.users.auth;

import fr.tripnjoy.users.api.response.JwtUserDetails;
import fr.tripnjoy.users.exception.TokenExpiredException;
import fr.tripnjoy.users.exception.TokenParsingException;
import fr.tripnjoy.users.exception.TokenVerificationException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;

@Component
public class TokenManager {

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

    public JwtUserDetails verifyToken(final String tokenString) throws TokenVerificationException
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
        long userId = claims.get("userId", Long.class);
        String username = claims.getSubject();
        return fromUserDetails(userId, userDetailsService.loadUserByUsername(username));
    }

    public JwtUserDetails fromUserDetails(final long userId, final UserDetails userDetails)
    {
        return new JwtUserDetails(
                userId,
                userDetails.getUsername(),
                userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList()
        );
    }
}
