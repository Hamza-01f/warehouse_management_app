package com.brief.demo.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class LoggingMdcFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request
                                    , HttpServletResponse response ,
                                    FilterChain filterChain) throws ServletException , IOException {
     try {
         MDC.put("env" , "dev");
         MDC.put("endpoint" , request.getMethod() + " " + request.getRequestURI());

         Authentication auth = SecurityContextHolder.getContext().getAuthentication();

         if(auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())){
             MDC.put("user : " , auth.getName());
             MDC.put("role and permissions : " , auth.getAuthorities().toString());
         }else{
             MDC.put("user" , "anonymouse");
             MDC.put("role" , "NONE");
         }

         filterChain.doFilter(request , response);
     }finally {
         MDC.put("status" , String.valueOf(response.getStatus()));
         MDC.clear();
     }


    }

}
