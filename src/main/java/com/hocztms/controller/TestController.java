package com.hocztms.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/index")
public class TestController {

    @RequestMapping("/test1")
    public String test1(HttpServletRequest request, HttpServletResponse response){
        System.out.println(request.getSession().getId());
        request.getSession().setAttribute("123","123");
        return "ok";
    }

    @RequestMapping("/test2")
    public String test2(HttpServletRequest request, HttpServletResponse response){
        System.out.println(request.getSession().getId());
        System.out.println(request.getSession().getAttribute("123"));
        return "ok";
    }
}
