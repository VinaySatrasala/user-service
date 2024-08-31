package com.capstone.user_service.util;

import com.capstone.user_service.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;


@Service
@Configuration

public class JwtUtil {
	
		String jwtSecret = "xucwefuwefgcuiwedqwdkjqbdjq$253235er2356";
		

        private SecretKey SECRET_KEY = io.jsonwebtoken.security.Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));

        public String extractUsername(String token) {
            return extractClaim(token, Claims::getSubject);
        }

        public Date extractExpiration(String token) {
            return extractClaim(token, Claims::getExpiration);
        }

        public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
            final Claims claims = extractAllClaims(token);
            return claimsResolver.apply(claims);
        }
        private Claims extractAllClaims(String token) {

//            return Jwts.parser().setSigningKey(SECRET_KEY).build().parseSignedClaims(token).getPayload();
        	return Jwts.parser().verifyWith(SECRET_KEY).build().parseSignedClaims(token).getPayload();    
        	 
        }

        private Boolean isTokenExpired(String token) {
            return extractExpiration(token).before(new Date());
        }

        public String generateToken(User userDetails) {
            Map<String, Object> claims = new HashMap<>();
            return createToken(claims, userDetails.getUsername());
        }

        private String createToken(Map<String, Object> claims, String subject) {
            //token set to expire afrer 1 minute just for demonstration purpose tho

//            return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
//                    .setExpiration(new Date(System.currentTimeMillis() + 1000*60))
//                    .signWith(SignatureAlgorithm.HS256, SECRET_KEY).compact();
        	
        	return Jwts.builder().claims(claims).subject(subject).issuedAt(new Date(System.currentTimeMillis()))
        			.expiration(new Date(System.currentTimeMillis() + 1000*60*60*2)).signWith(SECRET_KEY).compact();
        }

        public Boolean validateToken(String token, User userDetails) {
            final String username = extractUsername(token);
            return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
        }
}