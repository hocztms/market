package com.hocztms.springSecurity.xss;

import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
@WebFilter(filterName="xssFilter", urlPatterns="/*")
@SuppressWarnings("unchecked")
public class XssFilter implements Filter {

    private List<String> excludes = Arrays.asList(
            "/login", "/logout", ".html", ".jpeg", ".gif", ".jpg", ".png",".bmp",".JPEG",".GIF",".JPG",".PNG","BMP");


    @Override
    public void init(FilterConfig filterConfig)  {
        String excludesTemp = filterConfig.getInitParameter("excludes");
        if (excludesTemp != null) {
            String[] path = excludesTemp.split(",");
            excludes.addAll(Arrays.asList(path));
        }
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        if (isFilter(request)){
            filterChain.doFilter(servletRequest,servletResponse);
            return;
        }

        XssHttpServletRequestWrapper xssRequest = new XssHttpServletRequestWrapper((HttpServletRequest) request);
        filterChain.doFilter(xssRequest, servletResponse);

    }


    public boolean isFilter(HttpServletRequest request){
        String path = request.getServletPath();
        if (path==null) return false;
        return excludes.stream().anyMatch(method->path.endsWith(method)||path.matches(method));
    }
}
