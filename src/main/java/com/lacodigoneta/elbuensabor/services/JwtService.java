package com.lacodigoneta.elbuensabor.services;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.lacodigoneta.elbuensabor.entities.Image;
import com.lacodigoneta.elbuensabor.entities.User;
import com.lacodigoneta.elbuensabor.enums.Role;
import com.lacodigoneta.elbuensabor.exceptions.InvalidTokenException;
import com.lacodigoneta.elbuensabor.mappers.ImageMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;
import java.util.function.Function;

@Service
@Slf4j
public class JwtService {

    @Autowired
    private ImageMapper imageMapper;

    @Value("${security.key}")
    private String secretKey;

    @Autowired
    private GoogleIdTokenVerifier verifier;

    public String createToken(User user, boolean rememberMe) {

        int daysToExpiration = rememberMe ? 30 : 1;

        HashMap<String, Object> extraClaims = new HashMap<>();
        if (Objects.nonNull(user.getImage())) {
            extraClaims.put("image", imageMapper.toImageDto(user.getImage()).getLocation());
        } else {
            extraClaims.put("image", "https://objetivoligar.com/wp-content/uploads/2017/03/blank-profile-picture-973460_1280.jpg");
        }
        extraClaims.put("name", user.getName());
        extraClaims.put("lastName", user.getLastName());
        extraClaims.put("role", user.getRole());
        extraClaims.put("firstTimeAccess", user.isFirstTimeAccess());

        return Jwts.builder()
                .setClaims(extraClaims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24 * daysToExpiration))
                .setSubject(user.getUsername())
                .signWith(getSigningKey())
                .compact();
    }

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    public boolean isValid(String token, HttpServletRequest request) {
        try {
            return !isTokenExpired(token);
        } catch (ExpiredJwtException expiredJwtException) {
            log.info("Utils :: validateToke :: token Exception -> expired!");
            request.setAttribute("expired", expiredJwtException.getMessage());
            throw new ExpiredJwtException(expiredJwtException.getHeader(), expiredJwtException.getClaims(), "Token expirado");
        }
    }

    private boolean isTokenExpired(String token) {
        return extractExpirationDate(token).before(new Date(System.currentTimeMillis()));
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
        Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    private Date extractExpirationDate(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public User getUserFromGoogleToken(String idTokenString) throws GeneralSecurityException, IOException {

        GoogleIdToken idToken = verifier.verify(idTokenString);
        if (Objects.isNull(idToken)) {
            throw new InvalidTokenException();
        }
        Payload payload = idToken.getPayload();

        // Print user identifier
        String userId = payload.getSubject();

        // Get profile information from payload
        String email = payload.getEmail();
        boolean emailVerified = payload.getEmailVerified();
        String pictureUrl = (String) payload.get("picture");
        String familyName = (String) payload.get("family_name");
        String givenName = (String) payload.get("given_name");

        Image image;
        if (pictureUrl.isEmpty()) {
            image = Image.builder()
                    .location("https://objetivoligar.com/wp-content/uploads/2017/03/blank-profile-picture-973460_1280.jpg")
                    .build();
        } else {
            image = Image.builder()
                    .location(pictureUrl)
                    .build();
        }
        return User.builder()
                .username(email)
                .name(givenName)
                .lastName(familyName)
                .image(image)
                .emailConfirmed(emailVerified)
                .role(Role.USER)
                .build();

    }

}
