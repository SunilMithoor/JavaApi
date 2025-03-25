package com.app.security.jwt;

import com.app.config.LoggerService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

import static com.app.util.Utils.tagMethodName;


@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private HandlerExceptionResolver handlerExceptionResolver;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private LoggerService logger;

    private final String TAG = "JwtAuthenticationFilter";

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String methodName = "shouldNotFilter";
        String path = request.getRequestURI();
        boolean shouldNotFilter = path.equals("/swagger-ui/index.html") || path.equals("/api/auth/login") || path.equals("/api/users/register");
        logger.info(tagMethodName(TAG, methodName), " Request path: " + path + ", shouldNotFilter: " + shouldNotFilter);
        return shouldNotFilter;
    }


    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        String methodName = "doFilterInternal";
        logger.info(tagMethodName(TAG, methodName), " Processing request: " + request.getRequestURI());
        String fullUrl = request.getRequestURL().toString();
        String queryString = request.getQueryString();
        if (queryString != null) {
            fullUrl += "?" + queryString;
        }
        logger.info(tagMethodName(TAG, methodName), "Full Request URL: " + fullUrl);
        logger.info(tagMethodName(TAG, methodName), "Authorization Header: " + request.getHeader("Authorization"));
        final String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            logger.warn(tagMethodName(TAG, methodName), "No valid Authorization header found");
            filterChain.doFilter(request, response);
            return;
        }

        try {
            final String jwt = authHeader.substring(7);
            final String username = jwtUtil.extractUsername(jwt);
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (username != null && authentication == null) {
                logger.info(tagMethodName(TAG, methodName), "Valid token found for user: " + username);
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
                logger.info(tagMethodName(TAG, methodName), "User details: " + userDetails);
                if (jwtUtil.isTokenValid(jwt, userDetails.getUsername())) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    logger.info(tagMethodName(TAG, methodName), "User authenticated successfully: " + username);
                } else {
                    logger.warn(tagMethodName(TAG, methodName), "Token is invalid for user: " + username);
                }
            }

            filterChain.doFilter(request, response);
        } catch (ExpiredJwtException ex) {
            logger.warn(tagMethodName(TAG, methodName), "Token expired: " + ex.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        } catch (Exception exception) {
            logger.error(tagMethodName(TAG, methodName), "Unable to filter", exception);
            handlerExceptionResolver.resolveException(request, response, null, exception);
        }
    }
}
