package com.hocztms.springSecurity.jwt;

import com.hocztms.springSecurity.entity.MyUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@Component
public class JwtAuthTokenFilter extends OncePerRequestFilter{
    @Autowired
    private JwtAuthService jwtAuthService;

    @Autowired
    private JwtTokenUtils jwtTokenUtils;

    @Autowired
    private UserDetailsService userDetailServiceImpl;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = jwtAuthService.getToken(request);
        if (token!=null && token.length()>0) {
            String username = jwtAuthService.getTokenUsername(request);
            if (username != null && SecurityContextHolder.getContext().getAuthentication()==null) {
                MyUserDetails userDetails = (MyUserDetails) userDetailServiceImpl.loadUserByUsername(username);

                //判断token是否有效 包括 新旧token 判断 基于 user表 lastLoginDate字段判断
                if (jwtTokenUtils.validateToken(token, userDetails)) {
                    //给使用该JWT令牌的用户进行授权
                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    //设置用户身份授权
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            }
        }
        filterChain.doFilter(request, response);
    }
}
