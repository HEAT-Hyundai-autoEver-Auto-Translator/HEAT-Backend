package com.hyundaiautoever.HEAT.v1.util;

import com.hyundaiautoever.HEAT.v1.dto.user.LoginResponseDto;
import com.hyundaiautoever.HEAT.v1.entity.User;
import com.hyundaiautoever.HEAT.v1.repository.user.UserRepository;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Duration;
import java.time.LocalDate;
import java.util.Date;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Transactional
@Slf4j
public class JwtUtils {

    //    @Value("${spring.jwt.secret}")
    private String JWT_SECRET = "12345678901234567890121234567890123456789012!";
    private final String JWT_ISSUER = "HEAT";

    private final UserRepository userRepository;



    public String makeJwToken(User user, long duration) {
        Date now = new Date();

        Key key = Keys.hmacShaKeyFor(JWT_SECRET.getBytes(StandardCharsets.UTF_8));
        String jwToken = Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setIssuer(JWT_ISSUER)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + duration))
                .claim("userAccountNo", user.getUserAccountNo().toString())
                .claim("userName", user.getUserName())
                .claim("userRole", user.getUserRole().toString())
                .signWith(key)
                .compact();
        return jwToken;
    }

    public User validateRefreshToken(String refreshToken) {
        User user;
        Key key = Keys.hmacShaKeyFor(JWT_SECRET.getBytes(StandardCharsets.UTF_8));
        Jwt<?, ?> jwt;
        try {
            jwt = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .requireIssuer(JWT_ISSUER)
                    .build()
                    .parse(refreshToken);
            log.info(jwt.getBody().toString());
            Map<String, Object> claims = (Map<String, Object>) jwt.getBody();
            Long userAccountNo = Long.parseLong(claims.get("userAccountNo").toString());
            user = userRepository.findByUserAccountNo(userAccountNo);
            if (!refreshToken.equals(user.getRefreshToken())) {
                throw new JwtException("저장된 토큰과 불일치합니다.");
            }
        } catch(JwtException e){
            e.printStackTrace();
            throw e;
        }
        return user;
    }

    public User validateAccessToken (String accessToken) {
        Key key = Keys.hmacShaKeyFor(JWT_SECRET.getBytes(StandardCharsets.UTF_8));
        User user;
        Jwt<?, ?> jwt;
        try {
            jwt = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .requireIssuer(JWT_ISSUER)
                    .build()
                    .parse(accessToken);
            log.info(jwt.getBody().toString());
            Map<String, Object> claims = (Map<String, Object>) jwt.getBody();
            Long userAccountNo = Long.parseLong(claims.get("userAccountNo").toString());
            user = userRepository.findByUserAccountNo(userAccountNo);
        } catch(JwtException e){
            e.printStackTrace();
            return  null;
        }
        return user;
    }
}
