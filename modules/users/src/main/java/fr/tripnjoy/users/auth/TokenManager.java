package fr.tripnjoy.users.auth;

import fr.tripnjoy.users.api.exception.UserNotConfirmedException;
import fr.tripnjoy.users.api.response.JwtUserDetails;
import fr.tripnjoy.users.exception.TokenExpiredException;
import fr.tripnjoy.users.exception.TokenParsingException;
import fr.tripnjoy.users.exception.TokenVerificationException;
import fr.tripnjoy.users.model.UserModel;
import fr.tripnjoy.users.model.UserRole;
import fr.tripnjoy.users.service.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;

@Component
public class TokenManager {

    private final String jwtSecret;
    private final UserService userService;

    public TokenManager(@Value("${jwt.secret}") final String jwtSecret, final UserService userService)
    {
        this.jwtSecret = jwtSecret;
        this.userService = userService;
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
        return getUserDetails(userId);
    }

    private JwtUserDetails getUserDetails(long userId)
    {
        UserModel user = userService.findById(userId).orElseThrow(UserNotConfirmedException::new);
        return new JwtUserDetails(userId,
                user.getEmail(),
                user.getRoles().stream().map(UserRole::getAuthority).toList());
    }
}
