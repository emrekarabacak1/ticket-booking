package com.example.demo.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${application.security.jwt.secret-key}")
    private String secretKey;

    @Value("${application.security.jwt.expiration}")
    private Long jwtExpiration;

    // 2. TOKEN ÜRETME (Damga Basma)
    public String generateToken(String userName) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userName);
    }

    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims) // Ekstra bilgiler (Rol vs.)
                .setSubject(subject) // Damga kimin? (Ahmet)
                .setIssuedAt(new Date(System.currentTimeMillis())) // Ne zaman basıldı? (Şimdi)
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // 3. TOKEN OKUMA (Damgayı Okuma)
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // 4. TOKEN GEÇERLİ Mİ? (Sahte mi? Süresi dolmuş mu?)
    public boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        // Token üzerindeki isim ile veritabanındaki isim aynı mı? VE Süresi dolmamış mı?
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    // --- YARDIMCI METOTLAR (Burası işin mutfağı) ---

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey()) // Anahtarı veriyoruz ki şifreyi çözebilsin
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}