package com.app.security.jwt;

import com.app.config.LoggerService;
import com.app.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;


@Component
public class JwtUtil {

    @Autowired
    private LoggerService logger;

    private final String TAG = "JwtUtil";

    @Value("${security.jwt.secret-key}")
    private String secretKey;

    @Value("${security.jwt.expiration-time}")
    private long jwtExpiration;

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String extractUsername(String token) {
        String methodName = "extractUsername";
        try {
            return extractClaim(token, Claims::getSubject);
        } catch (Exception e) {
            logger.error(TAG, methodName, e);
            return null;
        }
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        String methodName = "extractClaim";
        try {
            final Claims claims = extractAllClaims(token);
            return claimsResolver.apply(claims);
        } catch (Exception e) {
            logger.error(TAG, methodName, e);
            return null;
        }
    }


//    public String generateToken(User user) {
//        Map<String, Object> extraClaims = new HashMap<>();
//        extraClaims.put("user_id", user.getId());
//        extraClaims.put("full_name", user.getFirstName() + " " + user.getLastName());
//        extraClaims.put("mobile_no", user.getMobileNo());
//        extraClaims.put("username", user.getUsername());
//        extraClaims.put("email_id", user.getEmailId());
//        return buildToken(extraClaims, user, jwtExpiration);
//    }

    public String generateToken(UserDetails userDetails) {
        Map<String, Object> extraClaims = new HashMap<>();
        if (userDetails instanceof User user) {
            extraClaims.put("user_id", user.getId());
            extraClaims.put("full_name", user.getFirstName() + " " + user.getLastName());
            extraClaims.put("mobile_no", user.getMobileNo());
            extraClaims.put("username", user.getUsername());
            extraClaims.put("email_id", user.getEmailId());
        }

        return buildToken(extraClaims, userDetails, jwtExpiration);
    }

    public long getExpirationTime() {
        return jwtExpiration;
    }

    private String buildToken(Map<String, Object> extraClaims, UserDetails user, long expiration) {
        String methodName = "buildToken";
        try {
            return Jwts.builder()
                    .setClaims(extraClaims)
                    .setSubject(user.getUsername())
                    .setIssuedAt(new Date(System.currentTimeMillis()))
                    .setExpiration(new Date(System.currentTimeMillis() + expiration))
                    .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                    .compact();
        } catch (Exception e) {
            logger.error(TAG, methodName, e);
            return null;
        }
    }

//    public boolean isTokenValid(String token, User user) {
//        String methodName = "isTokenValid";
//        try {
//            final String username = extractUsername(token);
//            boolean isValid = (username != null && username.equals(user.getUsername())) && !isTokenExpired(token);
//            logger.info(TAG, methodName + " Token validation result: " + isValid);
//            return isValid;
//        } catch (Exception e) {
//            logger.error(TAG, methodName, e);
//            return false;
//        }
//    }

    public boolean isTokenValid(String token, String username) {
        final String extractedUsername = extractUsername(token);
        return extractedUsername != null && extractedUsername.equals(username) && !isTokenExpired(token);
    }


    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        String methodName = "extractAllClaims";
        try {
            logger.info(TAG, "Extracting claims from token: " + token);
            return Jwts.parserBuilder()
                    .setSigningKey(getSignInKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            logger.error(TAG, methodName, e);
            logger.error(TAG, methodName + " [JwtUtil] Invalid JWT Token", e);
            throw new MalformedJwtException("Invalid JWT format");
        }
    }

    //    public String generateToken(String username) {
//        return Jwts.builder()
//                .setSubject(username)
//                .setIssuedAt(new Date())
//                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) // 1 hour validity
//                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
//                .compact();
//    }
}

