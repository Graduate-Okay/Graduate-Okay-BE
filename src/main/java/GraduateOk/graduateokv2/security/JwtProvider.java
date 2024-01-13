package GraduateOk.graduateokv2.security;

import GraduateOk.graduateokv2.dto.common.TokenResponse;
import GraduateOk.graduateokv2.exception.CustomException;
import GraduateOk.graduateokv2.exception.Error;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.security.Key;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;

@Component
public class JwtProvider {

    private final Key key;
    private static final String AUTHORITY_KEY = "auth";
    private static final long ACCESS_TOKEN_EXPIRE_TIME_MILLIS = 24L * 60L * 60L * 1000L; // 24시간
    private static final long REFRESH_TOKEN_EXPIRE_TIME_MILLIS = 30L * 24L * 60L * 60L * 1000L; // 30일

    public JwtProvider(@Value("${JWT_SECRET_KEY}") String secretKey) {
        byte[] secretKeyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(secretKeyBytes);
    }

    // 토큰 발급
    public TokenResponse generateToken(Long id, String role) {
        long now = new Date().getTime();

        return TokenResponse.builder()
                .tokenType("Bearer")
                .accessToken(generateAccessToken(id, role, now))
                .refreshToken(generateRefreshToken(id, now))
                .build();
    }

    // access token 발급
    public String generateAccessToken(Long id, String role, long now) {
        return Jwts.builder()
                .setSubject(id.toString())
                .claim(AUTHORITY_KEY, role) // 정보 저장
                .signWith(key, SignatureAlgorithm.HS256)
                .setExpiration(new Date(now + ACCESS_TOKEN_EXPIRE_TIME_MILLIS))
                .compact();
    }

    // refresh token 발급
    private String generateRefreshToken(Long id, long now) {
        return Jwts.builder()
                .setSubject(id.toString())
                .signWith(key, SignatureAlgorithm.HS256)
                .setExpiration(new Date(now + REFRESH_TOKEN_EXPIRE_TIME_MILLIS))
                .compact();
    }

    // 권한 정보 얻기
    public Authentication getAuthentication(String token) {
        Claims claims = parseClaims(token);

        if (ObjectUtils.isArray(claims.get(AUTHORITY_KEY))) {
            throw new CustomException(Error.INVALID_TOKEN);
        }

        Collection<? extends GrantedAuthority> authorities =
                Collections.singletonList(new SimpleGrantedAuthority(claims.get(AUTHORITY_KEY).toString()));

        return new UsernamePasswordAuthenticationToken(claims.getSubject(), "", authorities);
    }

    // 토큰 유효성 검사
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            throw new CustomException(Error.EXPIRED_TOKEN);
        } catch (Exception e) {
            throw new CustomException(Error.INVALID_TOKEN);
        }
    }

    // 토큰의 client 정보 디코딩
    private Claims parseClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }
}
