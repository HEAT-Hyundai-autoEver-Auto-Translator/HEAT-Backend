package com.hyundaiautoever.HEAT.v1.util;

import com.hyundaiautoever.HEAT.v1.entity.User;
import com.hyundaiautoever.HEAT.v1.repository.user.UserRepository;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.EntityNotFoundException;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Transactional
@Slf4j
public class JwtUtil {

    @Value("${spring.jwt.secret}")
    private String JWT_SECRET;
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
            Map<String, Object> claims = (Map<String, Object>) jwt.getBody();
            Long userAccountNo = Long.parseLong(claims.get("userAccountNo").toString());
            user = userRepository.findByUserAccountNo(userAccountNo)
                    .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 유저 정보입니다."));
            if (!StringUtils.hasText(refreshToken) && !refreshToken.equals(user.getRefreshToken())) {
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
            Map<String, Object> claims = (Map<String, Object>) jwt.getBody();
            Long userAccountNo = Long.parseLong(claims.get("userAccountNo").toString());
            user = userRepository.findByUserAccountNo(userAccountNo)
                    .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 유저 정보입니다."));
        } catch(JwtException e){
            e.printStackTrace();
            return  null;
        }
        return user;
    }
}
