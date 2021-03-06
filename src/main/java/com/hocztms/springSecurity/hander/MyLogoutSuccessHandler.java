package com.hocztms.springSecurity.hander;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class MyLogoutSuccessHandler implements LogoutSuccessHandler {
    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        response.setHeader("Content-type", "application/json;charset=utf-8");
        PrintWriter writer = response.getWriter();
        writer.write("{\"code\":\"1\",\"msg\":\"η»εΊζε\"}");
        writer.flush();
        writer.close();
    }
}
