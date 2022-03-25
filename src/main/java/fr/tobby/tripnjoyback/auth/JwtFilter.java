package fr.tobby.tripnjoyback.auth;

import fr.tobby.tripnjoyback.exception.auth.TokenVerificationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public final class JwtFilter extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(JwtFilter.class);

    private final TokenManager tokenManager;
    private final UserDetailsService userDetailsService;

    public JwtFilter(final TokenManager tokenManager, final UserDetailsService userDetailsService)
    {
        this.tokenManager = tokenManager;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response,
                                    final FilterChain filterChain) throws ServletException, IOException
    {
        String tokenHeader = request.getHeader("Authorization");
        if (tokenHeader == null || !tokenHeader.startsWith("Bearer "))
        {
            logger.debug("No jwt found [{}]", request.getRemoteAddr());
            filterChain.doFilter(request, response);
            return;
        }

        String token = tokenHeader.substring(7);
        UserDetails userDetails;
        try
        {
            userDetails = tokenManager.verifyToken(token);
        }
        catch (TokenVerificationException e)
        {
            logger.error("Failed to verify jwt", e);
            filterChain.doFilter(request, response);
            return;
        }
        logger.debug("Valid jwt [{}]", request.getRemoteAddr());
        // If jwt is found and valid, we authenticate the user so that he can access authenticated pages
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                userDetails, null,
                userDetails.getAuthorities());
        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        filterChain.doFilter(request, response);
    }
}
