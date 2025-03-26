package com.app.security.jwt;

import com.app.config.LoggerService;
import com.app.entity.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.Key;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import static com.app.util.Utils.tagMethodName;


@Component
public class JwtUtil {

    @Autowired
    private LoggerService logger;

    private final String TAG = "JwtUtil";

    @Value("${security.jwt.secret-key}")
    private String secretKey;

    @Value("${security.jwt.expiration-time}")
    private long jwtExpiration;

    @Autowired
    private StringRedisTemplate redisTemplate;


    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private PrivateKey readPrivateKey() throws Exception {

        // Read all bytes from private key file
        String filePath = "private_key.pem";
        String key = new String(Files.readAllBytes(Paths.get(filePath)));

        // Remove PEM header and footer
        key = key.replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s+", ""); // Remove all newlines and spaces

        // Decode Base64
        byte[] keyBytes = Base64.getDecoder().decode(key);

        // Convert to PrivateKey
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(keySpec);

    }


    private PublicKey readPublicKey() throws Exception {
        String filePath = "public_key.pem"; // Update path if needed
        String key = new String(Files.readAllBytes(Paths.get(filePath)));

        key = key.replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s+", ""); // Removes newlines and spaces

        //  Decode only the cleaned Base64 key
        byte[] publicKeyBytes = Base64.getDecoder().decode(key);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");

        return keyFactory.generatePublic(keySpec);
    }

    private boolean verifyToken(String token) throws Exception {
        PublicKey publicKey = readPublicKey();
        try {
            Jwts.parserBuilder().setSigningKey(publicKey).build().parseClaimsJws(token);
            return true; // Valid token
        } catch (Exception e) {
            return false; // Invalid token
        }
    }

    public String extractUsername(String token) {
        String methodName = "extractUsername";
        logger.info(tagMethodName(TAG, methodName), "Extract username: " + token);
        try {
            return extractClaim(token, Claims::getSubject);
        } catch (Exception e) {
            logger.error(tagMethodName(TAG, methodName), "Unable to extract username", e);
            return null;
        }
    }

    public Long extractUserId(String token) {
        String methodName = "extractUserId";
        logger.info(tagMethodName(TAG, methodName), "Extract user id: " + token);
        try {
            return extractClaim(token, claims -> claims.get("user_id", Long.class));
        } catch (Exception e) {
            logger.error(tagMethodName(TAG, methodName), "Unable to extract id", e);
            return null;
        }
    }

    public String extractUserRole(String token) {
        String methodName = "extractUserRole";
        try {
            return extractClaim(token, claims -> claims.get("role", String.class));
        } catch (Exception e) {
            logger.error(tagMethodName(TAG, methodName), "Unable to extract role", e);
            return null;
        }
    }


    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        String methodName = "extractClaim";
        try {
            final Claims claims = extractAllClaims(token);
//            for (Map.Entry<String, Object> entry : claims.entrySet()) {
//                logger.info(tagMethodName(TAG, methodName), entry.getKey() + ": " + entry.getValue());
//            }
            return claimsResolver.apply(claims);
        } catch (Exception e) {
            logger.error(tagMethodName(TAG, methodName), "Unable to extract claim", e);
            return null;
        }
    }


    public String generateToken(User user) {
        String methodName = "generateToken";
        try {
            Map<String, Object> extraClaims = new HashMap<>();
            extraClaims.put("user_id", user.getId());
//            extraClaims.put("first_name", user.getFirstName());
//            extraClaims.put("last_name", user.getLastName());
//            extraClaims.put("mobile_no", user.getMobileNo());
//            extraClaims.put("username", user.getUsername());
//            extraClaims.put("email_id", user.getEmailId());
            extraClaims.put("role", user.getRole());
            logger.info(tagMethodName(TAG, methodName), "Token claims: " + user);
            return buildTokenRS256(extraClaims, user, jwtExpiration);
        } catch (Exception e) {
            logger.error(tagMethodName(TAG, methodName), "Unable to generate token", e);
            return null;
        }
    }

    public long getExpirationTime(String token) {
        String methodName = "getExpirationTime";
        try {
            Date expirationDate = extractClaim(token, Claims::getExpiration);
            if (expirationDate != null) {
                long expirationMillis = expirationDate.getTime() - System.currentTimeMillis();
                logger.info(tagMethodName(TAG, methodName), "Token expiration time (ms): " + expirationMillis);
                return Math.max(expirationMillis, 0); // Ensure non-negative time
            }
        } catch (Exception e) {
            logger.error(tagMethodName(TAG, methodName), "Unable to extract expiration time", e);
        }
        return 0;
    }


    public long getExpirationTime() {
        String methodName = "getExpirationTime";
        logger.info(tagMethodName(TAG, methodName), "Jwt expiration: " + jwtExpiration);
        return jwtExpiration;
    }

    private String buildTokenRS256(Map<String, Object> extraClaims, UserDetails user, long expiration) {
        String methodName = "buildToken";
        try {
            return Jwts.builder()
                    .setClaims(extraClaims)
                    .setSubject(user.getUsername())
                    .setIssuedAt(new Date(System.currentTimeMillis()))
                    .setExpiration(new Date(System.currentTimeMillis() + expiration))
                    .signWith(readPrivateKey(), SignatureAlgorithm.RS256)
                    .compact();
        } catch (Exception e) {
            logger.error(tagMethodName(TAG, methodName), "Unable to build token", e);
            return null;
        }
    }


    private String buildTokenHS256(Map<String, Object> extraClaims, UserDetails user, long expiration) {
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
            logger.error(tagMethodName(TAG, methodName), "Unable to build token", e);
            return null;
        }
    }


    public boolean isTokenValid(String token, String username) {
        String methodName = "isTokenValid";
        try {
            logger.info(tagMethodName(TAG, methodName), " Token: " + token);
            logger.info(tagMethodName(TAG, methodName), " Username: " + username);
            boolean isTokenBlacklisted = isTokenBlacklisted(token);
            if (isTokenBlacklisted) {
                logger.info(tagMethodName(TAG, methodName), " Token blacklisted: true ");
                return false;  // Token is blacklisted
            }

            final String extractedUsername = extractUsername(token);

            boolean isValid = extractedUsername != null && extractedUsername.equals(username) && !isTokenExpired(token);
            logger.info(tagMethodName(TAG, methodName), " Token validation result: " + isValid);
            return isValid;
        } catch (Exception e) {
            logger.error(tagMethodName(TAG, methodName), "Invalid token", e);
            return false;
        }
    }

    public void invalidateToken(String token) {
        String methodName = "invalidateToken";
        try {
            long remainingTime = getExpirationTime(token);
            logger.info(tagMethodName(TAG, methodName), " Token remaining time: " + remainingTime);
            blacklistToken(token, remainingTime);
        } catch (Exception e) {
            logger.error(tagMethodName(TAG, methodName), "Invalid token", e);
        }
    }

    private boolean isTokenExpired(String token) {
        String methodName = "isTokenExpired";
        boolean isTokenExpired = extractExpiration(token).before(new Date());
        logger.info(tagMethodName(TAG, methodName), "Token expired: " + isTokenExpired);
        return isTokenExpired;
    }

    private Date extractExpiration(String token) {
        String methodName = "extractExpiration";
        Date date = extractClaim(token, Claims::getExpiration);
        logger.info(tagMethodName(TAG, methodName), "Token expiration: " + date);
        return date;
    }

    private Claims extractAllClaims(String token) {
        String methodName = "extractAllClaims";
        try {
            logger.info(tagMethodName(TAG, methodName), "Extracting claims from token: " + token);
//            Claims claims = Jwts.parserBuilder()
//                    .setSigningKey(getSignInKey())
//                    .build()
//                    .parseClaimsJws(token)
//                    .getBody();
//            // Log all claims explicitly
//            claims.forEach((key, value) -> logger.info(tagMethodName(TAG, methodName), key + ": " + value));

            PublicKey publicKey = readPublicKey();
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(publicKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            // Log all claims explicitly
            claims.forEach((key, value) -> logger.info(tagMethodName(TAG, methodName), key + ": " + value));

            return claims;
        } catch (ExpiredJwtException e) {
            logger.error(tagMethodName(TAG, methodName), "JWT Token has expired", e);
            throw new ExpiredJwtException(e.getHeader(), e.getClaims(), "JWT token is expired");
        } catch (SignatureException e) {
            logger.error(tagMethodName(TAG, methodName), "Invalid JWT signature", e);
            throw new SignatureException("Invalid JWT signature");
        } catch (MalformedJwtException e) {
            logger.error(tagMethodName(TAG, methodName), "Malformed JWT token", e);
            throw new MalformedJwtException("Malformed JWT token");
        } catch (Exception e) {
            logger.error(tagMethodName(TAG, methodName), "Invalid JWT Token", e);
            throw new RuntimeException("Invalid JWT token");
        }
    }

    // Store user's current token in Redis
    public void storeUserToken(String userId, String token, long expirationMillis) {
        String methodName = "storeUserToken";
        try {
            long expirationSeconds = expirationMillis / 1000; // Convert to seconds
            redisTemplate.opsForValue().set("USER_TOKEN_" + userId, token, expirationSeconds, TimeUnit.SECONDS);
        } catch (Exception e) {
            logger.error(tagMethodName(TAG, methodName), "Unable to store user token", e);
        }
    }

    // Retrieve user's stored token from Redis
    public String getUserToken(String userId) {
        String methodName = "getUserToken";
        try {
            return redisTemplate.opsForValue().get("USER_TOKEN_" + userId);
        } catch (Exception e) {
            logger.error(tagMethodName(TAG, methodName), "Unable to get user token", e);
            return null;
        }
    }

    // Blacklist token by storing it with expiration time
    public void blacklistToken(String token, long expirationMillis) {
        String methodName = "blacklistToken";
        try {
            long expirationSeconds = expirationMillis / 1000; // Convert to seconds
            redisTemplate.opsForValue().set("BLACKLIST_" + token, "true", expirationSeconds, TimeUnit.SECONDS);
        } catch (Exception e) {
            logger.error(tagMethodName(TAG, methodName), "Unable to blacklist token", e);
        }
    }

    // Check if token is blacklisted
    public boolean isTokenBlacklisted(String token) {
        String methodName = "isTokenBlacklisted";
        try {
            return Boolean.TRUE.equals(redisTemplate.hasKey("BLACKLIST_" + token));
        } catch (Exception e) {
            logger.error(tagMethodName(TAG, methodName), "Unable to validate blacklisted token", e);
            return false;
        }
    }
}

