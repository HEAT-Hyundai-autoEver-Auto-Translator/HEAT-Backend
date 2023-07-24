package com.hyundaiautoever.HEAT.v1.filter;

import com.hyundaiautoever.HEAT.v1.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String accessToken = parseAccessToken(request);
        if (accessToken != null) {
            com.hyundaiautoever.HEAT.v1.entity.User userEntity = jwtUtil.validateAccessToken(accessToken);
            if (userEntity != null) {
                Authentication authentication = getAuthentication(accessToken, userEntity);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token");
            }
        } else {
            // 토큰이 제공되지 않았을 경우
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "No token provided");
        }
        filterChain.doFilter(request, response);
    }

    // Authentication 발급 메소드
    private Authentication getAuthentication(String jwToken, com.hyundaiautoever.HEAT.v1.entity.User userEntity) {
        UserDetails userDetails = User.withUsername(userEntity.getUserName())
                .password("password")   // Jwt 방식 인증으로 패스워드는 사용 하지 않음, 현재 임시값 'password' 적용
                .roles(userEntity.getUserRole().toString().toUpperCase())
                .build();
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    //액세스 토큰 파싱 메소드
    private String parseAccessToken(HttpServletRequest request) {
        String accessToken = request.getHeader("Authorization");
        if (StringUtils.hasText(accessToken) && accessToken.startsWith("Bearer ")) {
            return accessToken.substring(7, accessToken.length());
        }
        return null;
    }
}
