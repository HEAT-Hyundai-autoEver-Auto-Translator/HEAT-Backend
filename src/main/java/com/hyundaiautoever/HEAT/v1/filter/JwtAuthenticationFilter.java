package com.hyundaiautoever.HEAT.v1.filter;

import com.hyundaiautoever.HEAT.v1.service.LoginService;
import com.hyundaiautoever.HEAT.v1.util.JwtUtils;
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

    private final JwtUtils jwtUtils;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String accessToken = parseAccessToken(request);
        if (accessToken != null) {
            com.hyundaiautoever.HEAT.v1.entity.User userEntity = jwtUtils.validateAccessToken(accessToken);
            if (userEntity != null) {
                Authentication authentication = getAuthentication(accessToken, userEntity);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        filterChain.doFilter(request, response);
    }

    private Authentication getAuthentication(String jwToken, com.hyundaiautoever.HEAT.v1.entity.User userEntity) {
        UserDetails userDetails = User.withUsername(userEntity.getUserName())
                .password("password")
                .roles(userEntity.getUserRole().toString().toUpperCase())
                .build();
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    private String parseAccessToken(HttpServletRequest request) {
        String accessToken = request.getHeader("Authorization");
        log.error(accessToken);
        if (StringUtils.hasText(accessToken) && accessToken.startsWith("Bearer ")) {
            return accessToken.substring(7, accessToken.length());
        }
        return null;
    }


}
