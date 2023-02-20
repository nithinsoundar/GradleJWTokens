package com.nithin.gradlejwttokens.AuthenticationService.AuthenticationConfig;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtFilter extends GenericFilterBean {

    @Override
    public void doFilter(ServletRequest servletRequest,
                         ServletResponse servletResponse,
                         FilterChain filterChain)
            throws IOException, ServletException {
        final HttpServletRequest request = (HttpServletRequest) servletRequest;
        final HttpServletResponse response = (HttpServletResponse) servletResponse;
        final String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")){
            final String token = authHeader.substring(7);
            Claims claims = Jwts
                    .parser()
                    .setSigningKey("6A576E5A7234753778214125442A472D4B6150645367556B5870327335763879")
                    .parseClaimsJws(token)
                    .getBody();
            request.setAttribute("claims", claims);
            request.setAttribute("blog", servletRequest.getParameter("id"));
        }
        filterChain.doFilter(request, response);

    }
}