package com.app.security.jwt;

import com.app.config.LoggerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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
        logger.info(tagMethodName(TAG, methodName), "Processing request: " + request.getRequestURI());
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

                // Check if the token is blacklisted
                if (jwtUtil.isTokenBlacklisted(jwt)) {
                    logger.warn(tagMethodName(TAG, methodName), "Token is blacklisted for user: " + username);
                    sendErrorResponse(response, HttpStatus.UNAUTHORIZED, "Token has been revoked. Please log in again.");
                    return;
                }

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
                    sendErrorResponse(response, HttpStatus.UNAUTHORIZED, "Invalid token. Please log in again.");
                    return;
                }
            }

            filterChain.doFilter(request, response);
        } catch (ExpiredJwtException ex) {
            logger.warn(tagMethodName(TAG, methodName), "Token expired: " + ex.getMessage());
            sendErrorResponse(response, HttpStatus.UNAUTHORIZED, "Token has expired. Please log in again.");
        } catch (SignatureException ex) {
            logger.error(tagMethodName(TAG, methodName), "Invalid JWT Signature", ex);
            sendErrorResponse(response, HttpStatus.UNAUTHORIZED, "Invalid JWT Signature. Please check your token.");
        } catch (MalformedJwtException ex) {
            logger.error(tagMethodName(TAG, methodName), "Malformed JWT", ex);
            sendErrorResponse(response, HttpStatus.UNAUTHORIZED, "Malformed JWT. Please use a valid token.");
        } catch (Exception exception) {
            logger.error(tagMethodName(TAG, methodName), "Unable to filter", exception);
            handlerExceptionResolver.resolveException(request, response, null, exception);
        }
    }


    /**
     * Helper method to send structured error response.
     */
//    private void sendErrorResponse(HttpServletResponse response, HttpStatus status, String message) throws IOException {
//        response.setContentType("application/json");
//        response.setStatus(status.value());
//        response.getWriter().write(new ObjectMapper().writeValueAsString(ResponseHandler.failure(status, message)));
//    }
    private void sendErrorResponse(HttpServletResponse response, HttpStatus status, String message) throws IOException {
        response.setContentType("application/json");
        response.setStatus(status.value());

        // Creating response in the expected format
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", new Date());
        errorResponse.put("status_code", status.value());
        errorResponse.put("success", false);
        errorResponse.put("data", null);
        errorResponse.put("message", message);

        response.getWriter().write(new ObjectMapper().writeValueAsString(errorResponse));
    }
}
