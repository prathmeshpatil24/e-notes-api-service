package com.enotes.security;

import com.enotes.entity.RoleEntity;
import com.enotes.entity.UserEntity;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class JWTService {

    @Value("${jwt.secret}")
    private  String jwtSecret;

    // Secret key (in-memory for now, move to config/env in production)
//    public static final Key SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    // Token validity in milliseconds (e.g., 60 minutes)
    private static final long EXPIRATION_TIME = 7 * 24 * 60 * 60 * 1000;
            //1000 * 60 * 60;

    // signing key helper
    public Key getSigningKey() {
//        return SECRET_KEY;
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    //extract all claims (decoder)
    private Claims extractAllClaims(String token){
        return Jwts.parserBuilder()
                //.setSigningKey(SECRET_KEY)// provide the secret key
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)// decode and verify the token
                .getBody();
    }

    public String extractUserName(String token) {
        Claims claims = extractAllClaims(token);
        return claims.getSubject();
    }
    
    //extract role
    public List<String>extractRoles(String token){
        Claims claims = extractAllClaims(token);
        return claims.get("roles", List.class);
    }
    
    //extract active status
    public Boolean extractIsActive(String token){
        Claims claims = extractAllClaims(token);
        Boolean active = claims.get("active", Boolean.class);
        return active != null && active;
    }

    public Date expirationDate(String token){
        Claims claims = extractAllClaims(token);
        return claims.getExpiration();
    }

    // Checks if token has expired
    private boolean isTokenExpired(String token) {
        return expirationDate(token).before(new Date());
    }

    //generate the token with role and isActive
    public String generateAccessToken(UserEntity userEntity) {

        Map<String,Object> claims = new HashMap<>();

        claims.put("roles", userEntity.getRoles()
                .stream()
                .map(RoleEntity::getRoleName) //ROLE_USER, ROLE_ADMIN
                .toList()
        );

        claims.put("active", userEntity.getIsActive());

        // Build and return JWT token
        return Jwts.builder()
                .setClaims(claims)// custom claims (optional)
                .setSubject(userEntity.getEmail())// subject (e.g., username, email)
                .setIssuedAt(new Date(System.currentTimeMillis())) // issue time
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) // expiry time
                //.signWith(SECRET_KEY) // sign with secret key
                .signWith(getSigningKey())
                .compact();
    }

    public boolean validateToken(String token, UserDetails userDetails) {

        String userName = extractUserName(token);// extract from token
        // must match and not expired
        return userName.equals(userDetails.getUsername())
                && !isTokenExpired(token)
                && extractIsActive(token);
    }
}
