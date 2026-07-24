package com.amdox.nexushr.leave.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;


@Component
public class JwtAuthFilter extends OncePerRequestFilter {


    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {


        String path = request.getRequestURI();


        if(path.startsWith("/swagger-ui")
                || path.startsWith("/v3/api-docs")
                || path.startsWith("/webjars")
                || path.startsWith("/actuator")) {

            filterChain.doFilter(request,response);
            return;
        }



        String header =
                request.getHeader("Authorization");



        if(header != null && header.startsWith("Bearer ")) {


            String token =
                    header.substring(7);



            /*
              TODO:
              Validate JWT signature here
              using Auth Service secret
            */



            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(

                            "f5bd50ff-2ae3-4636-976c-89d081d92c3a",

                            null,

                            List.of(
                                new SimpleGrantedAuthority(
                                    "ROLE_EMPLOYEE"
                                )
                            )
                    );



            SecurityContextHolder
                    .getContext()
                    .setAuthentication(authentication);



            System.out.println(
                "JWT AUTH SET SUCCESS"
            );

        }



        filterChain.doFilter(
                request,
                response
        );

    }

}